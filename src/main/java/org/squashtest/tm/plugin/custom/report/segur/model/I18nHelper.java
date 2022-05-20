/*
 * Copyright ANS 2020-2022
 */
package org.squashtest.tm.plugin.custom.report.segur.model;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component("custom.report.segur.i18nHelper")
public class I18nHelper implements MessageSourceAware {

	protected MessageSource msgSource;

	protected Locale currentLocale() {
		Locale current = LocaleContextHolder.getLocale();
		if (current == null) {
			current = Locale.getDefault();
		}
		return current;
	}

	public String translate(String string) {
		if (StringUtils.isEmpty(string)) {
			return StringUtils.EMPTY;
		}
		return msgSource.getMessage(string, null, currentLocale());
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		msgSource = messageSource;

	}
}
