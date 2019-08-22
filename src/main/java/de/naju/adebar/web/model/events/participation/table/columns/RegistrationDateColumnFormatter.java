package de.naju.adebar.web.model.events.participation.table.columns;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import de.naju.adebar.model.events.Event;
import de.naju.adebar.model.events.RegistrationInfo;
import de.naju.adebar.model.persons.Person;
import de.naju.adebar.web.model.events.participation.table.ParticipantsTable;

/**
 * Formatter used in the {@link ParticipantsTable} to display the registration date of participants.
 *
 * @author Rico Bergmann
 */
@Service
public class RegistrationDateColumnFormatter implements TableColumnFormatter {

	private final MessageSource messageSource;

	/**
	 * Constructs a new formatter.
	 *
	 * @param messageSource which contains default messages if a date of birth is not set. Must not be
	 *        {@code null}.
	 */
	public RegistrationDateColumnFormatter(MessageSource messageSource) {
		Assert.notNull(messageSource, "MessageSource may not be null");
		this.messageSource = messageSource;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.naju.adebar.web.model.events.participation.table.columns.TableColumnFormatter#isApplicable(
	 * de.naju.adebar.model.events.Event)
	 */
	@Override
	public boolean isApplicable(Event event) {
		// the registration dates may always be displayed
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see de.naju.adebar.web.model.events.participation.table.columns.TableColumnFormatter#
	 * formatColumnFor(de.naju.adebar.model.persons.Person, de.naju.adebar.model.events.Event)
	 */
	@Override
	public String formatColumnFor(Person participant, Event event) {
		final RegistrationInfo registrationInfo =
				event.getParticipantsList().getParticipationDetailsFor(participant);

		if (registrationInfo.hasRegistrationDate()) {
			final LocalDateTime registrationDate = registrationInfo.getRegistrationDate();
			final long rawTimeSinceRegistration =
					ChronoUnit.DAYS.between(LocalDate.from(registrationDate), LocalDate.now().plusDays(1L));
			final Date registrationDateAsOldDate =
					Date.from(registrationDate.atZone(ZoneId.systemDefault()).toInstant());
			final Object[] messageArgs = {registrationDateAsOldDate};

			int timeSinceRegistration = Integer.MAX_VALUE;
			if (rawTimeSinceRegistration < Integer.MAX_VALUE) {
				timeSinceRegistration = (int) rawTimeSinceRegistration;
			}

			switch (timeSinceRegistration) {
				case 0:
					return messageSource.getMessage("today.with-flex-time", messageArgs,
							LocaleContextHolder.getLocale());
				case 1:
					return messageSource.getMessage("yesterday.with-flex-time", messageArgs,
							LocaleContextHolder.getLocale());
				case 2:
					return messageSource.getMessage("ereyesterday.with-flex-time", messageArgs,
							LocaleContextHolder.getLocale());
				default:
					return messageSource.getMessage("flex-datetime",
							new Object[] {registrationDateAsOldDate, registrationDateAsOldDate},
							LocaleContextHolder.getLocale());
			}
		} else {
			return messageSource.getMessage("field.unknown", new Object[] {},
					LocaleContextHolder.getLocale());
		}

	}

}
