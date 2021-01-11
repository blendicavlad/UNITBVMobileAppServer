package com.vlad.app.service;

import com.vlad.app.exception.BadRequestException;
import com.vlad.app.exception.ResourceNotFoundException;
import com.vlad.app.model.Class;
import com.vlad.app.model.Department;
import com.vlad.app.model.Discipline;
import com.vlad.app.model.Group;
import com.vlad.app.model.Professor;
import com.vlad.app.model.Specialization;
import com.vlad.app.model.Student;
import com.vlad.app.model.UserType;
import com.vlad.app.repository.ClassRepository;
import com.vlad.app.repository.DepartmentRepository;
import com.vlad.app.repository.DisciplineRepository;
import com.vlad.app.repository.ExamRepository;
import com.vlad.app.repository.GradeRepository;
import com.vlad.app.repository.GroupRepository;
import com.vlad.app.repository.ProfessorRepository;
import com.vlad.app.repository.SpecializationRepository;
import com.vlad.app.repository.StudentRepository;
import com.vlad.app.repository.UserRepository;
import com.vlad.app.service.email.EmailServiceImpl;
import com.vlad.app.service.parsing.CSVParserFactory;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Log4j2
public class AdminDataHandlingService {

	private final static Logger logger = LoggerFactory.getLogger(AdminDataHandlingService.class);

	private final EmailServiceImpl emailService;
	private final StudentRepository studentRepository;
	private final ProfessorRepository professorRepository;
	private final UserRepository userRepository;
	private final DepartmentRepository departmentRepository;
	private final SpecializationRepository specializationRepository;
	private final ClassRepository classRepository;
	private final GroupRepository groupRepository;
	private final DisciplineRepository disciplineRepository;
	private final ExamRepository examRepository;
	private final GradeRepository gradeRepository;
	private final PasswordEncoder passwordEncoder;
	private final CSVParserFactory csvParserFactory;

	public AdminDataHandlingService(EmailServiceImpl emailService, StudentRepository studentRepository,
			ProfessorRepository professorRepository, UserRepository userRepository,
			DepartmentRepository departmentRepository,
			SpecializationRepository specializationRepository, ClassRepository classRepository,
			GroupRepository groupRepository, DisciplineRepository disciplineRepository,
			ExamRepository examRepository, GradeRepository gradeRepository,
			PasswordEncoder passwordEncoder,
			CSVParserFactory csvParserFactory) {
		this.emailService = emailService;
		this.studentRepository = studentRepository;
		this.professorRepository = professorRepository;
		this.userRepository = userRepository;
		this.departmentRepository = departmentRepository;
		this.specializationRepository = specializationRepository;
		this.classRepository = classRepository;
		this.groupRepository = groupRepository;
		this.disciplineRepository = disciplineRepository;
		this.examRepository = examRepository;
		this.gradeRepository = gradeRepository;
		this.passwordEncoder = passwordEncoder;
		this.csvParserFactory = csvParserFactory;
	}

	/**
	 *
	 * @return no of inserted records or exception
	 * @throws ResourceNotFoundException
	 * @throws BadRequestException
	 */

	/**
	 *
	 * @return no of inserted records or exception
	 * @throws ResourceNotFoundException
	 * @throws BadRequestException
	 */
	@SneakyThrows
	public int uploadProfessors(MultipartFile multipartFile, boolean sendEmails)
			throws ResourceNotFoundException, BadRequestException {

		var emailAndPasswords = new HashMap<String, String>();
		return csvParserFactory.getParser(Professor.class).parse(multipartFile.getInputStream()).stream()
				.map(Professor.class::cast)
				.peek(professor -> professorRepository.findByCNP(professor.getCNP()).ifPresentOrElse((prof -> {
					prof.getUser().setEmail(professor.getUser().getEmail());
					prof.getUser().setFirstName(professor.getUser().getFirstName());
					prof.getUser().setLastName(professor.getUser().getLastName());
					professorRepository.save(prof);
					log.info("Updated professor: " + prof.toString());
				}), () -> {
					professor.getUser().setUserType(UserType.PROFESSOR);
					var password = generateRandomPassword();
					professor.getUser().setPassword(passwordEncoder.encode(password));
					emailAndPasswords.put(professor.getUser().getEmail(), password);
					professorRepository.save(professor);
					log.info("Saved new professor: " + professor.toString());
				})).collect(Collectors.toList())
				.stream()
				.peek(professor -> {
							if (sendEmails) {
								emailService.sendSimpleMessage(
										professor.getUser().getEmail(),
										"UNITBV APP Account created",
										String.format("Email: %s \nPassword: %s",
												professor.getUser().getEmail(),
												emailAndPasswords.get(professor.getUser().getEmail()))
								);
							}
						}
				).collect(Collectors.counting()).intValue();
	}

	@SneakyThrows
	public int uploadDepartments(MultipartFile multipartFile)
			throws ResourceNotFoundException, BadRequestException {
		return csvParserFactory.getParser(Department.class)
				.parse(multipartFile.getInputStream())
				.stream()
				.map(dept -> {
					log.info("Saved new department: " + dept.toString());
					return departmentRepository.save((Department) dept);
				}).collect(Collectors.counting()).intValue();
	}

	@SneakyThrows
	public int uploadSpecializations(MultipartFile multipartFile) {
		return csvParserFactory
				.getParser(Specialization.class)
				.parse(multipartFile.getInputStream())
				.stream()
				.map(spec -> {
					log.info("Saved new specialization: " + spec.toString());
					return specializationRepository.save((Specialization) spec);
				})
				.collect(Collectors.counting()).intValue();
	}

	@SneakyThrows
	@Transactional
	public int uploadDisciplines(MultipartFile multipartFile) {
		return csvParserFactory
				.getParser(Discipline.class)
				.parse(multipartFile.getInputStream())
				.stream()
				.map(disc -> {
					log.info("Saved new discipline: " + disc.toString());
					return disciplineRepository.save((Discipline) disc);
				})
				.collect(Collectors.counting()).intValue();
	}

	@SneakyThrows
	public int uploadClasses(MultipartFile multipartFile) {
		return csvParserFactory
				.getParser(Class.class)
				.parse(multipartFile.getInputStream())
				.stream()
				.map(clss -> {
					log.info("Saved new group: " + clss.toString());
					return classRepository.save((Class) clss);
				})
				.collect(Collectors.counting()).intValue();
	}

	@SneakyThrows
	public int uploadGroups(MultipartFile multipartFile) {
		return csvParserFactory
				.getParser(Group.class)
				.parse(multipartFile.getInputStream())
				.stream()
				.map(group -> {
					log.info("Saved new group: " + group.toString());
					return groupRepository.save((Group) group);
				})
				.collect(Collectors.counting()).intValue();
	}

	@SneakyThrows
	public int uploadStudents(MultipartFile multipartFile, boolean sendEmails)
			throws ResourceNotFoundException, BadRequestException {
		var emailAndPasswords = new HashMap<String, String>();
		return studentRepository
				.saveAll(csvParserFactory.getParser(Student.class).parse(multipartFile.getInputStream()).stream()
						.map(Student.class::cast)
						.peek(student -> studentRepository.findByCNP(student.getCNP()).ifPresentOrElse((stud -> {
							stud.getUser().setEmail(student.getUser().getEmail());
							stud.getUser().setFirstName(student.getUser().getFirstName());
							stud.getUser().setLastName(student.getUser().getLastName());
							studentRepository.save(stud);
						}), () -> {
							student.getUser().setUserType(UserType.STUDENT);
							var password = generateRandomPassword();
							student.getUser().setPassword(passwordEncoder.encode(password));
							emailAndPasswords.put(student.getUser().getEmail(), password);
							studentRepository.save(student);
							log.info("Saved new student: " + student.toString());
						})).collect(Collectors.toList()))
				.stream()
				.peek(professor -> {
							if (sendEmails) {
								emailService.sendSimpleMessage(
										professor.getUser().getEmail(),
										"UNITBV APP Account created",
										String.format("Email: %s \nPassword: %s",
												professor.getUser().getEmail(),
												emailAndPasswords.get(professor.getUser().getEmail()))
								);
							}
						}
				).collect(Collectors.counting()).intValue();
	}


	public String wipeData(boolean wipeDevData) {
		try {
			if (wipeDevData) {
				studentRepository.deleteAll();
				groupRepository.deleteAll();
				gradeRepository.deleteAll();
				examRepository.deleteAll();
				classRepository.deleteAll();
				disciplineRepository.deleteAll();
				specializationRepository.deleteAll();
				departmentRepository.deleteAll();
				professorRepository.deleteAll();
			} else {
				studentRepository.findAll().stream()
						.filter(student -> !student.getUser().getEmail()
								.equals(DataLoader.DevDataConstants.STUDENT_MAIL))
						.forEach(studentRepository::delete);
				groupRepository.findAll().stream()
						.filter(group -> !group.getId().equals(DataLoader.DevDataConstants.GROUP_ID))
						.forEach(groupRepository::delete);
				gradeRepository.deleteAll();
				examRepository.deleteAll();
				classRepository.findAll().stream()
						.filter(aClass -> !aClass.getId().equals(DataLoader.DevDataConstants.CLASS_ID))
						.forEach(classRepository::delete);
				disciplineRepository.findAll().stream()
						.filter(discipline -> !discipline.getId().equals(DataLoader.DevDataConstants.DISCIPLINE_ID))
						.forEach(disciplineRepository::delete);
				specializationRepository.findAll().stream()
						.filter(specialization -> !specialization.getId()
								.equals(DataLoader.DevDataConstants.SPECIALIZATION_ID))
						.forEach(specializationRepository::delete);
				departmentRepository.findAll().stream()
						.filter(department -> !department.getId().equals(DataLoader.DevDataConstants.DEPARTMENT_ID))
						.forEach(departmentRepository::delete);
				professorRepository.findAll().stream()
						.filter(professor -> !professor.getUser().getEmail()
								.equals(DataLoader.DevDataConstants.PROFESSOR_MAIL))
						.forEach(professorRepository::delete);
			}
		} catch (Exception e) {
			return "Failed to delete entities: " + e.getMessage();
		}
		return "Data wiped successfully";
	}

	private String generateRandomPassword() {
		return new RandomStringGenerator.Builder()
				.selectFrom(new String(IntStream.rangeClosed(32, 126).toArray(), 0, 95).toCharArray())
				.withinRange(10, 30)
				.build()
				.generate(15);
	}

}
