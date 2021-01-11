package com.vlad.app.controller;

import com.vlad.app.payload.ApiResponse;
import com.vlad.app.service.AdminDataHandlingService;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;
import java.util.function.Function;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	private final AdminDataHandlingService dataHandler;

	public AdminController(AdminDataHandlingService dataHandler) {
		this.dataHandler = dataHandler;
	}

	@PostMapping("/upload_students")
	public ResponseEntity<ApiResponse> uploadStudents(@ApiParam @RequestParam("file") MultipartFile file,
			@RequestParam boolean sendEmails) {
		return uploadData(file, (f) -> dataHandler.uploadStudents(f, sendEmails));
	}

	@PostMapping("/upload_professors")
	public ResponseEntity<ApiResponse> uploadProfessors(@ApiParam @RequestParam("file") MultipartFile file,
			@RequestParam boolean sendEmails) {
		return uploadData(file, (f) -> dataHandler.uploadProfessors(f, sendEmails));
	}

	@PostMapping("/upload_departments")
	public ResponseEntity<ApiResponse> uploadDepartments(@ApiParam @RequestParam("file") MultipartFile file) {
		return uploadData(file, (f) -> dataHandler.uploadDepartments(file));
	}

	@PostMapping("/upload_specializations")
	public ResponseEntity<ApiResponse> uploadSpecializations(@ApiParam @RequestParam("file") MultipartFile file) {
		return uploadData(file, (f) -> dataHandler.uploadSpecializations(file));
	}

	@PostMapping("/upload_disciplines")
	public ResponseEntity<ApiResponse> uploadDisciplines(@ApiParam @RequestParam("file") MultipartFile file) {
		return uploadData(file, (f) -> dataHandler.uploadDisciplines(file));
	}

	@PostMapping("/upload_classes")
	public ResponseEntity<ApiResponse> uploadClasses(@ApiParam @RequestParam("file") MultipartFile file) {
		return uploadData(file, (f) -> dataHandler.uploadClasses(file));
	}

	@PostMapping("/upload_groups")
	public ResponseEntity<ApiResponse> uploadGroups(@ApiParam @RequestParam("file") MultipartFile file) {
		return uploadData(file, (f) -> dataHandler.uploadGroups(file));
	}

	@PostMapping("/wipe_data")
	public ResponseEntity<ApiResponse> wipeData(@ApiParam @RequestParam("wipeDevData") Boolean wipeDevData) {
		return ResponseEntity.ok().body(new ApiResponse(true, dataHandler.wipeData(wipeDevData)));
	}

	private ResponseEntity<ApiResponse> uploadData(MultipartFile file,
			Function<MultipartFile, Integer> uploadFunction) {
		if (file == null || file.isEmpty())
			return ResponseEntity.status(HttpStatus.NO_CONTENT)
					.body(new ApiResponse(false, "No file found in request"));

		if (!(Objects.equals(file.getContentType(), "text/csv") ||
				Objects.equals(file.getContentType(), "application/vnd.ms-excel"))) {
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
					.body(new ApiResponse(false, "File must be of csv or MS Excel type"));
		}
		return ResponseEntity.ok().body(new ApiResponse(true,
				String.format("Inserted or Updated %s records", uploadFunction.apply(file))));
	}
}
