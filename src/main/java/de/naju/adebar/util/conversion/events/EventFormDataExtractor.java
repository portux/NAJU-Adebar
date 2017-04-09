package de.naju.adebar.util.conversion.events;

import de.naju.adebar.controller.forms.events.EventForm;
import de.naju.adebar.controller.forms.events.EventForm.*;
import de.naju.adebar.model.events.Event;
import de.naju.adebar.model.human.Address;
import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static de.naju.adebar.controller.forms.events.EventForm.CURRENCY_UNIT;

/**
 * Service to extract the necessary data from a 'add event' or 'edit event' form
 * @author Rico Bergmann
 * @see EventForm
 */
@Service
public class EventFormDataExtractor {

    private DateTimeFormatter dateTimeFormatter;

    public EventFormDataExtractor() {
        this.dateTimeFormatter = DateTimeFormatter.ofPattern(EventForm.DATE_TIME_FORMAT, Locale.GERMAN);
    }

    /**
     * @param eventForm the form containing the data to extract
     * @return the {@link Event} object encoded by the form
     */
    public Event extractEvent(EventForm eventForm) {
        Address address = new Address(eventForm.getStreet(), eventForm.getZip(), eventForm.getCity());

        Event event = new Event(eventForm.getName(), LocalDateTime.parse(eventForm.getStartTime(), dateTimeFormatter), LocalDateTime.parse(eventForm.getEndTime(), dateTimeFormatter));

        event.setPlace(address);

        if (eventForm.hasParticipantsAge()) {
            event.setMinimumParticipantAge(Integer.parseInt(eventForm.getParticipantsAge()));
        }

        if (eventForm.hasParticipantsLimit()) {
            event.setParticipantsLimit(Integer.parseInt(eventForm.getParticipantsLimit()));
        }

        if (eventForm.hasParticipationFee()) {
            event.setParticipationFee(Money.of(new BigDecimal(eventForm.getParticipationFee()), CURRENCY_UNIT));
        }

        return event;
    }

    public Belonging extractBelonging(EventForm eventForm) {
        return Belonging.valueOf(eventForm.getBelonging());
    }
}
