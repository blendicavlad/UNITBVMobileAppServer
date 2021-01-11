package com.vlad.app.service;

import com.vlad.app.controller.AuthController;
import com.vlad.app.model.AuthProvider;
import com.vlad.app.model.Class;
import com.vlad.app.model.Department;
import com.vlad.app.model.Discipline;
import com.vlad.app.model.Group;
import com.vlad.app.model.Specialization;
import com.vlad.app.model.User;
import com.vlad.app.model.UserType;
import com.vlad.app.payload.LoginRequest;
import com.vlad.app.payload.SignUpRequest;
import com.vlad.app.repository.ClassRepository;
import com.vlad.app.repository.DepartmentRepository;
import com.vlad.app.repository.DisciplineRepository;
import com.vlad.app.repository.GroupRepository;
import com.vlad.app.repository.ProfessorRepository;
import com.vlad.app.repository.SpecializationRepository;
import com.vlad.app.repository.UserRepository;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Component
public class DataLoader implements ApplicationRunner {

	private final PasswordEncoder passwordEncoder;
	private final Environment environment;
	private final UserRepository userRepository;
	private final DisciplineRepository disciplineRepository;
	private final ProfessorRepository professorRepository;
	private final SpecializationRepository specializationRepository;
	private final ClassRepository classRepository;
	private final GroupRepository groupRepository;
	private final DepartmentRepository departmentRepository;
	private final AuthController authController;
	private final AdminDataHandlingService dataUploadService;

	public DataLoader(PasswordEncoder passwordEncoder, Environment environment,
			UserRepository userRepository, DisciplineRepository disciplineRepository,
			ProfessorRepository professorRepository,
			SpecializationRepository specializationRepository, ClassRepository classRepository,
			GroupRepository groupRepository, DepartmentRepository departmentRepository,
			AuthController authController,
			AdminDataHandlingService dataUploadService) {
		this.passwordEncoder = passwordEncoder;
		this.environment = environment;
		this.userRepository = userRepository;
		this.disciplineRepository = disciplineRepository;
		this.professorRepository = professorRepository;
		this.specializationRepository = specializationRepository;
		this.classRepository = classRepository;
		this.groupRepository = groupRepository;
		this.departmentRepository = departmentRepository;
		this.authController = authController;
		this.dataUploadService = dataUploadService;
	}

	public static final class DevDataConstants {
		public final static String PROFESSOR_MAIL = "prof@gmail.com";
		public final static String PROFESSOR_FIRST_NAME = "Profesor";
		public final static String PROFESSOR_LAST_NAME = "Profesorescu";
		public final static String PROFESSOR_CNP = "1232145986512";
		public final static String PASSWORD = "test";
		public final static String DEPARTMENT_ID = "AN";
		public final static String DEPARTMENT_NAME = "Antreprenourship";
		public final static String SPECIALIZATION_ID = "ANS";
		public final static String SPECIALIZATION_NAME = "Sales and Entrepreneurship";
		public final static String DISCIPLINE_ID = "SAL01";
		public final static String DISCIPLINE_NAME = "SAL01";
		public final static String DISCIPLINE_DESCRIPTION = "Sales";
		public final static String STUDENT_MAIL = "student@gmail.com";
		public final static String STUDENT_FIRST_NAME = "Student";
		public final static String STUDENT_LAST_NAME = "Studentescu";
		public final static String STUDENT_CNP = "1232145986519";
		public final static String CLASS_ID = "CL01ANS";
		public final static int CLASS_YEAR = 2020;
		public final static String GROUP_ID = "LFAND001";
	}

	@Override public void run(ApplicationArguments args) {
		System.out.println("=== START DATA INITIALIZATION ===");
		initData();
	}

	void initData() {
		initSuperUser();
		if (Boolean.parseBoolean(environment.getProperty("init.loadmock"))) {
			initMockFiles();
		} else {
			System.out.println("init.loadmock is set to false, not initializing mock data");
		}
		initDevTestData();
		System.out.println("=== DATA INITIALIZATION COMPLETE ===");
	}


	void initDevTestData() {

		System.out.println("INIT Test Professor: " + userRepository.findByEmail(DevDataConstants.PROFESSOR_MAIL)
				.orElseGet(() -> {
					var req = new SignUpRequest(DevDataConstants.PROFESSOR_FIRST_NAME,
							DevDataConstants.PROFESSOR_LAST_NAME, DevDataConstants.PROFESSOR_MAIL,
							DevDataConstants.PASSWORD,
							UserType.PROFESSOR,
							DevDataConstants.PROFESSOR_CNP, null);
					authController.registerUser(req);
					var prof = professorRepository.findByCNP(DevDataConstants.PROFESSOR_CNP).get();
					var department = new Department(DevDataConstants.DEPARTMENT_ID, DevDataConstants.DEPARTMENT_NAME,
							Set.of());
					department = departmentRepository.save(department);
					System.out.println("INIT Test Department: " + department.getName());
					var specialization = new Specialization(DevDataConstants.SPECIALIZATION_ID,
							DevDataConstants.SPECIALIZATION_NAME, Set.of(), Set.of(),
							department);
					specialization = specializationRepository.save(specialization);
					System.out.println("INIT Test Specialization: " + specialization.getName());
					var discipline = new Discipline(DevDataConstants.DISCIPLINE_ID, DevDataConstants.DISCIPLINE_NAME,
							DevDataConstants.DISCIPLINE_DESCRIPTION, prof, specialization,
							Set.of());
					System.out.println("INIT Test Discipline: " + discipline.getName());
					disciplineRepository.save(discipline);
					return prof.getUser();
				}).getEmail());

		authSuperUser();

		System.out.println("INIT Test Student: " + userRepository.findByEmail(DevDataConstants.STUDENT_MAIL)
				.orElseGet(() -> {
					var clss = new Class(DevDataConstants.CLASS_ID, DevDataConstants.CLASS_YEAR, Set.of(),
							specializationRepository.findById(DevDataConstants.SPECIALIZATION_ID).get());
					classRepository.save(clss);
					System.out.println("INIT Test Class: " + clss.getId());
					var group = new Group(DevDataConstants.GROUP_ID, clss, Set.of());
					groupRepository.save(group);
					System.out.println("INIT Test Group: " + group.getId());
					var req = new SignUpRequest(DevDataConstants.STUDENT_FIRST_NAME, DevDataConstants.STUDENT_LAST_NAME,
							DevDataConstants.STUDENT_MAIL, DevDataConstants.PASSWORD, UserType.STUDENT,
							DevDataConstants.STUDENT_CNP, DevDataConstants.GROUP_ID);
					authController.registerUser(req);
					return userRepository.findByEmail("student@gmail.com").get();
				}).getEmail());

		authSuperUser();
	}

	void initSuperUser() {
		System.out.println("INIT SuperUser: superuser@gmail.com");
		userRepository.findByEmail("superuser@gmail.com")
				.orElseGet(() -> {
							var user = new User(
									"Andrei",
									"Testescu",
									"superuser@gmail.com",
									passwordEncoder.encode(environment.getProperty("init.userpass")),
									AuthProvider.local,
									UserType.ADMIN);
							user.setAdmin(true);
							userRepository.save(user);
							return user;
						}
				);
		authSuperUser();
	}

	void authSuperUser() {
		var loginRequest = new LoginRequest("superuser@gmail.com", environment.getProperty("init.userpass"));
		authController.authenticateUser(loginRequest);
	}

	void initMockFiles() {
		BiConsumer<String, Function<MultipartFile, Integer>> loadData = (fileName, f) -> {
			BiConsumer<File, FileItem> loadFile = (file, fileItem) -> {
				InputStream input;
				OutputStream os;
				try {
					input = new FileInputStream(file);
					os = fileItem.getOutputStream();
					IOUtils.copy(input, os);
					input.close();
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
			InputStream fileContent;
			fileContent = Objects
					.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("data/" + fileName));
			File file = new File("file.csv");
			try {
				FileUtils.copyInputStreamToFile(fileContent, file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("CREATED FILE: " + file.toString() + " WITH LENGTH: " + file.length());
			FileItem diskFileItem = new DiskFileItem("file", "text/csv", false, file.getName(), (int) file.length(),
					file.getParentFile());
			loadFile.accept(file, diskFileItem);
			var multiPartFile = new CommonsMultipartFile(diskFileItem);
			f.apply(multiPartFile);
		};
		loadData.accept("professors.csv", (f) -> dataUploadService.uploadProfessors(f, false));
		loadData.accept("departments.csv", dataUploadService::uploadDepartments);
		loadData.accept("specializations.csv", dataUploadService::uploadSpecializations);
		loadData.accept("disciplines.csv", dataUploadService::uploadDisciplines);
		loadData.accept("classes.csv", dataUploadService::uploadClasses);
		loadData.accept("groups.csv", dataUploadService::uploadGroups);
		loadData.accept("students.csv", (f) -> dataUploadService.uploadStudents(f, false));
	}

}
