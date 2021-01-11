package com.vlad.app.csvconverters;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.bean.BeanField;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvBadConverterException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

public class AutowiredConverterMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {

	private final AutowireCapableBeanFactory beanFactory;

	public AutowiredConverterMappingStrategy(
			AutowireCapableBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	@Override protected BeanField<T, String> instantiateCustomConverter(
			Class<? extends AbstractBeanField<T, String>> converter) throws CsvBadConverterException {
		BeanField<T, String> c = super.instantiateCustomConverter(converter);
		this.beanFactory.autowireBean(c);
		return c;
	}
}
