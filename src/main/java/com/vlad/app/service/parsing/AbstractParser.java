package com.vlad.app.service.parsing;

import java.io.InputStream;
import java.util.List;

public interface AbstractParser<T> {
	List<T> parse(InputStream inputStream);
}
