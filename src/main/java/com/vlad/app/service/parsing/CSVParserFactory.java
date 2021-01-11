package com.vlad.app.service.parsing;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.MappingStrategy;
import com.vlad.app.csvconverters.AutowiredConverterMappingStrategy;
import com.vlad.app.model.Department;
import com.vlad.app.model.Discipline;
import com.vlad.app.model.Group;
import com.vlad.app.model.Professor;
import com.vlad.app.model.Specialization;
import com.vlad.app.model.Class;
import com.vlad.app.model.Student;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@Component
public class CSVParserFactory {

	private final AutowireCapableBeanFactory beanFactory;

	public CSVParserFactory(AutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	public AbstractParser<?> getParser(java.lang.Class<?> type) {

		if (type.isAssignableFrom(Professor.class)) {
			return new ProfessorParser(this.beanFactory);
		} else if (type.isAssignableFrom(Department.class)) {
			return new DepartmentsParser(this.beanFactory);
		} else if (type.isAssignableFrom(Specialization.class)) {
			return new SpecializationParser(this.beanFactory);
		} else if (type.isAssignableFrom(Discipline.class)) {
			return new DisciplinesParser(this.beanFactory);
		} else if (type.isAssignableFrom(Class.class)) {
			return new ClassesParser(this.beanFactory);
		} else if (type.isAssignableFrom(Group.class)) {
			return new GroupsParser(this.beanFactory);
		} else if (type.isAssignableFrom(Student.class)) {
			return new StudentsParser(this.beanFactory);
		} else {
			throw new RuntimeException("Could not determine type of the object to be parsed");
		}

	}

	private static record ProfessorParser(AutowireCapableBeanFactory beanFactory)
			implements AbstractParser<Professor> {

		@Override public List<Professor> parse(InputStream inputStream) {
			MappingStrategy<Professor> professorMappingStrategy = new AutowiredConverterMappingStrategy<>(this.beanFactory);
			professorMappingStrategy.setType(Professor.class);
			return new CsvToBeanBuilder<Professor>(new InputStreamReader(inputStream))
					.withType(Professor.class)
					.withSeparator(';')
					.withMappingStrategy(professorMappingStrategy)
					.build()
					.parse();
		}
	}

	private static record DepartmentsParser(AutowireCapableBeanFactory beanFactory)
			implements AbstractParser<Department> {

		@Override public List<Department> parse(InputStream inputStream) {
			MappingStrategy<Department> departmentMappingStrategy = new AutowiredConverterMappingStrategy<>(
					this.beanFactory);
			departmentMappingStrategy.setType(Department.class);
			return new CsvToBeanBuilder<Department>(new InputStreamReader(inputStream))
					.withSeparator(';')
					.withMappingStrategy(departmentMappingStrategy)
					.withType(Department.class)
					.build()
					.parse();
		}
	}

	private static record SpecializationParser(
			AutowireCapableBeanFactory beanFactory)
			implements AbstractParser<Specialization> {

		@Override public List<Specialization> parse(InputStream inputStream) {
			MappingStrategy<Specialization> specializationMappingStrategy = new AutowiredConverterMappingStrategy<>(
					this.beanFactory);
			specializationMappingStrategy.setType(Specialization.class);
			return new CsvToBeanBuilder<Specialization>(new InputStreamReader(inputStream))
					.withSeparator(';')
					.withMappingStrategy(specializationMappingStrategy)
					.withType(Specialization.class)
					.build()
					.parse();
		}
	}

	private static record DisciplinesParser(AutowireCapableBeanFactory beanFactory)
			implements AbstractParser<Discipline> {

		@Override public List<Discipline> parse(InputStream inputStream) {
			MappingStrategy<Discipline> disciplineMappingStrategy = new AutowiredConverterMappingStrategy<>(
					this.beanFactory);
			disciplineMappingStrategy.setType(Discipline.class);
			return new CsvToBeanBuilder<Discipline>(new InputStreamReader(inputStream))
					.withMappingStrategy(disciplineMappingStrategy)
					.withType(Discipline.class)
					.withSeparator(';')
					.build()
					.parse();
		}
	}

	private static record ClassesParser(AutowireCapableBeanFactory beanFactory)
			implements AbstractParser<Class> {

		@Override public List<Class> parse(InputStream inputStream) {
			MappingStrategy<Class> classMappingStrategy = new AutowiredConverterMappingStrategy<>(
					this.beanFactory);
			classMappingStrategy.setType(Class.class);
			return new CsvToBeanBuilder<Class>(new InputStreamReader(inputStream))
					.withMappingStrategy(classMappingStrategy)
					.withType(Class.class)
					.withSeparator(';')
					.build()
					.parse();
		}
	}

	private static record GroupsParser(AutowireCapableBeanFactory beanFactory)
			implements AbstractParser<Group> {

		@Override public List<Group> parse(InputStream inputStream) {
			MappingStrategy<Group> groupMappingStrategy = new AutowiredConverterMappingStrategy<>(
					this.beanFactory);
			groupMappingStrategy.setType(Group.class);
			return new CsvToBeanBuilder<Group>(new InputStreamReader(inputStream))
					.withMappingStrategy(groupMappingStrategy)
					.withType(Group.class)
					.withSeparator(';')
					.build()
					.parse();
		}
	}

	private static record StudentsParser(AutowireCapableBeanFactory beanFactory)
			implements AbstractParser<Student> {

		@Override public List<Student> parse(InputStream inputStream) {
			MappingStrategy<Student> studentMappingStrategy = new AutowiredConverterMappingStrategy<>(
					this.beanFactory);
			studentMappingStrategy.setType(Student.class);
			return new CsvToBeanBuilder<Student>(new InputStreamReader(inputStream))
					.withMappingStrategy(studentMappingStrategy)
					.withType(Student.class)
					.withSeparator(';')
					.build()
					.parse();
		}
	}

}
