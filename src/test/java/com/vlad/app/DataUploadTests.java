package com.vlad.app;

import com.vlad.app.config.JpaConfig;
import com.vlad.app.controller.AdminController;
import com.vlad.app.controller.AuthController;
import com.vlad.app.model.Department;
import com.vlad.app.payload.ApiResponse;
import com.vlad.app.payload.LoginRequest;
import com.vlad.app.repository.ClassRepository;
import com.vlad.app.repository.DepartmentRepository;
import com.vlad.app.repository.DisciplineRepository;
import com.vlad.app.repository.GroupRepository;
import com.vlad.app.repository.ProfessorRepository;
import com.vlad.app.repository.SpecializationRepository;
import com.vlad.app.repository.StudentRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.Objects;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppApplication.class, JpaConfig.class})
public class DataUploadTests {

	@Autowired
	private AuthController authController;
	@Autowired
	private Environment environment;
	@Autowired
	private AdminController adminController;
	@Autowired
	private ProfessorRepository professorRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private DisciplineRepository disciplineRepository;
	@Autowired
	private SpecializationRepository specializationRepository;
	@Autowired
	private ClassRepository classRepository;
	@Autowired
	private GroupRepository groupRepository;
	@Autowired
	private StudentRepository studentRepository;

	private static boolean SEND_EMAILS = false;

	public DataUploadTests() {
	}

	@Before
	public void authUser() {

		var loginRequest = new LoginRequest("superuser@gmail.com", environment.getProperty("init.userpass"));
		authController.authenticateUser(loginRequest);
	}

	@Test
	public void testUploadData() throws Exception {
		/**
		 * Upload Professors
		 */
		var apiResponse = uploadProfessors();
		Assert.assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
		Assert.assertEquals("TEST UPLOAD PROFESSORS", "Inserted or Updated 50 records",
				Objects.requireNonNull(apiResponse.getBody()).message());
		professorRepository.findByCNP("1057376568577").ifPresentOrElse(professor -> {
			Assert.assertEquals("Gail", professor.getUser().getFirstName());
		}, () -> Assert.fail("Professor with CNP 1057376568577 was not found"));
		Assert.assertEquals(51, professorRepository.count());

		/**
		 * Upload Departments
		 */
		apiResponse = uploadDepartments();
		Assert.assertEquals("TEST UPLOAD DEPARTMENTS", "Inserted or Updated 2 records",
				Objects.requireNonNull(Objects.requireNonNull(apiResponse.getBody()).message()));
		Assert.assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
		departmentRepository.findById("MN").ifPresentOrElse(department -> {
			Assert.assertEquals("Management", department.getName());
		}, () -> Assert.fail("Department with ID MN was not found"));
		Assert.assertEquals(3, departmentRepository.count());

		/**
		 * Upload Specializations
		 */
		apiResponse = uploadSpecializations();
		Assert.assertEquals("TEST UPLOAD SPECIALIZATIONS", "Inserted or Updated 4 records",
				Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(apiResponse.getBody()).message())));
		Assert.assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
		specializationRepository.findById("IE").ifPresentOrElse(specialization -> {
			Assert.assertEquals("Informatica Economica", specialization.getName());
		}, () -> Assert.fail("Specialization with ID IE was not found"));
		Assert.assertEquals(5, specializationRepository.count());

		/**
		 * Upload Discciplines
		 */
		apiResponse = uploadDisciplines();
		Assert.assertEquals("TEST UPLOAD DISCIPLINES", "Inserted or Updated 5 records",
				Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(apiResponse.getBody()).message())));
		Assert.assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
		disciplineRepository.findById("MN01").ifPresentOrElse(discipline -> {
			Assert.assertEquals("Management", discipline.getName());
		}, () -> Assert.fail("Discipline with MN01 IE was not found"));
		Assert.assertEquals(6, disciplineRepository.count());
		specializationRepository.findAll().forEach(specialization -> {
			Assert.assertTrue(specialization.getDisciplines().size() > 0);
		});

		/**
		 * Upload Classes
		 */
		apiResponse = uploadClasses();
		Assert.assertEquals("TEST UPLOAD CLASSES", "Inserted or Updated 4 records",
				Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(apiResponse.getBody()).message())));
		Assert.assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
		classRepository.findById("CL01MNM").ifPresentOrElse(clss -> {
			Assert.assertEquals("Management", clss.getSpecialization().getName());
		}, () -> Assert.fail("Class with CL01MNM IE was not found"));

		/**
		 * Upload Groups
		 */
		apiResponse = uploadGroups();
		Assert.assertEquals("TEST UPLOAD GROUPS", "Inserted or Updated 8 records",
				Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(apiResponse.getBody()).message())));
		Assert.assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
		groupRepository.findById("LFIE002").ifPresentOrElse(group -> {
			Assert.assertEquals("Informatica Economica", group.getClss().getSpecialization().getName());
		}, () -> Assert.fail("Class with CL01MNM IE was not found"));
		classRepository.findAll().forEach(clss -> {
			Assert.assertTrue(clss.getGroups().size() > 0);
		});

		/**
		 * Upload Students
		 */
		apiResponse = uploadStudents();
		Assert.assertEquals(HttpStatus.OK, apiResponse.getStatusCode());
		Assert.assertEquals("TEST UPLOAD STUDENTS", "Inserted or Updated 50 records",
				Objects.requireNonNull(apiResponse.getBody()).message());
		studentRepository.findByCNP("1007704839383").ifPresentOrElse(student -> {
			Assert.assertEquals("fermentum.metus@eget.co.uk", student.getUser().getEmail());
		}, () -> Assert.fail("Student with CNP 1007704839383 was not found"));

	}

	private ResponseEntity<ApiResponse> uploadProfessors() throws IOException {
		var fileContent = Objects
				.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("files/professors.csv"));
		var file = new MockMultipartFile("professors.csv", "professors.csv", "text/csv", fileContent.readAllBytes());
		return adminController.uploadProfessors(file, SEND_EMAILS);
	}

	private ResponseEntity<ApiResponse> uploadDepartments() throws IOException {
		var fileContent = Objects
				.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("files/departments.csv"));
		var file = new MockMultipartFile("departments.csv", "departments.csv", "text/csv", fileContent.readAllBytes());
		return adminController.uploadDepartments(file);
	}

	private ResponseEntity<ApiResponse> uploadSpecializations() throws IOException {
		var fileContent = Objects
				.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("files/specializations.csv"));
		var file = new MockMultipartFile("specializations.csv", "specializations.csv", "text/csv", fileContent.readAllBytes());
		return adminController.uploadSpecializations(file);
	}

	private ResponseEntity<ApiResponse> uploadDisciplines() throws IOException {
		var fileContent = Objects
				.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("files/disciplines.csv"));
		var file = new MockMultipartFile("disciplines.csv", "disciplines.csv", "text/csv", fileContent.readAllBytes());
		return adminController.uploadDisciplines(file);
	}

	private ResponseEntity<ApiResponse> uploadClasses() throws IOException {
		var fileContent = Objects
				.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("files/classes.csv"));
		var file = new MockMultipartFile("classes.csv", "classes.csv", "text/csv", fileContent.readAllBytes());
		return adminController.uploadClasses(file);
	}

	private ResponseEntity<ApiResponse> uploadGroups() throws IOException {
		var fileContent = Objects
				.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("files/groups.csv"));
		var file = new MockMultipartFile("groups.csv", "groups.csv", "text/csv", fileContent.readAllBytes());
		return adminController.uploadGroups(file);
	}

	private ResponseEntity<ApiResponse> uploadStudents() throws IOException {
		var fileContent = Objects
				.requireNonNull(this.getClass().getClassLoader().getResourceAsStream("files/students.csv"));
		var file = new MockMultipartFile("students.csv", "students.csv", "text/csv", fileContent.readAllBytes());
		return adminController.uploadStudents(file, false);
	}
}
