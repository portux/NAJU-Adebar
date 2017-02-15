package de.naju.adebar.controller;

import de.naju.adebar.app.human.filter.PersonFilterBuilder;
import de.naju.adebar.controller.forms.*;
import de.naju.adebar.model.human.*;
import de.naju.adebar.util.conversion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Person related controller mappings
 *
 * @author Rico Bergmann
 * @see Person
 * @see Activist
 * @see Referent
 */
@Controller
public class PersonController {
    private HumanManager humanManager;
    private QualificationManager qualificationManager;
    private CreatePersonFormDataExtractor createPersonFormDataExtractor;
    private EditPersonFormDataExtractor editPersonFormDataExtractor;
    private FilterPersonFormFilterExtractor filterPersonFormFilterExtractor;


    @Autowired
    public PersonController(HumanManager humanManager, QualificationManager qualificationManager, CreatePersonFormDataExtractor createPersonFormDataExtractor,
                            EditPersonFormDataExtractor editPersonFormDataExtractor, FilterPersonFormFilterExtractor filterPersonFormFilterExtractor) {
        Object[] params = {humanManager, qualificationManager, createPersonFormDataExtractor, editPersonFormDataExtractor, filterPersonFormFilterExtractor};
        Assert.noNullElements(params, "At least one parameter was null: " + Arrays.toString(params));
        this.humanManager = humanManager;
        this.createPersonFormDataExtractor = createPersonFormDataExtractor;
        this.editPersonFormDataExtractor = editPersonFormDataExtractor;
        this.filterPersonFormFilterExtractor = filterPersonFormFilterExtractor;
        this.qualificationManager = qualificationManager;
    }

    /**
     * Displays the person overview
     * @param model model to display the data in
     * @return the persons' overview view
     */
    @RequestMapping("/persons")
    public String showPersonOverview(Model model) {
        model.addAttribute("addPersonForm", new CreatePersonForm());
        model.addAttribute("filterPersonsForm", new FilterPersonForm());
        model.addAttribute("persons", humanManager.personManager().repository().findFirst25ByOrderByLastName());
        model.addAttribute("qualifications", qualificationManager.repository().findAll());
        return "persons";
    }

    /**
     * Displays the person overview with all persons
     * @param model model to display the data in
     * @return the persons' overview view
     */
    @RequestMapping("/persons/all")
    public String showAllPersons(Model model) {
        model.addAttribute("addPersonForm", new CreatePersonForm());
        model.addAttribute("filterPersonsForm", new FilterPersonForm());
        model.addAttribute("persons", humanManager.personManager().repository().findAll());
        model.addAttribute("qualifications", qualificationManager.repository().findAll());
        return "persons";
    }

    /**
     * Adds a new person to the database
     * @param createPersonForm person object created by the model
     * @return the new person's detail view
     */
    @RequestMapping("/persons/add")
    public String addPerson(@ModelAttribute("addPersonFrom") CreatePersonForm createPersonForm) {
        Person person = humanManager.savePerson(createPersonFormDataExtractor.extractPerson(createPersonForm));
        if (createPersonForm.isActivist()) {
            Activist activist = humanManager.createActivist(person);
            createPersonFormDataExtractor.extractJuleicaExpiryDate(createPersonForm).ifPresent(d -> {
                activist.setJuleicaExpiryDate(d);
                humanManager.activistManager().saveActivist(activist);
            });
        }
        if (createPersonForm.isReferent()) {
            Referent referent = humanManager.createReferent(person);
            createPersonFormDataExtractor.extractQualifications(createPersonForm, qualificationManager.repository()).ifPresent(q -> {
                    referent.addAllQualifications(q);
                    humanManager.referentManager().saveReferent(referent);
            });
        }

        return "redirect:/persons/" + person.getId();
    }

    /**
     * Filters the saved persons
     * @param filterPersonForm filter object created by the model
     * @param model model to display the data in
     * @return the persons' overview consisting of the persons that matched the filter
     */
    @RequestMapping("/persons/filter")
    public String filterPersons(@ModelAttribute("filterPersonsForm") FilterPersonForm filterPersonForm, Model model) {
        List<Person> persons = humanManager.personManager().repository().streamAll().collect(Collectors.toList());
        PersonFilterBuilder filterBuilder = new PersonFilterBuilder(persons.stream());
        filterPersonFormFilterExtractor.extractAllFilters(filterPersonForm).forEach(filterBuilder::applyFilter);
        model.addAttribute("persons", filterBuilder.filter());
        model.addAttribute("addPersonForm", new CreatePersonForm());
        model.addAttribute("filterPersonsForm", new FilterPersonForm());
        return "persons";
    }

    /**
     * Detail view for persons
     * @param personId the id of the person to display
     * @param model model to display the data in
     * @return the person's detail view
     */
    @RequestMapping("/persons/{pid}")
    public String showPersonDetails(@PathVariable("pid") String personId, Model model) {

        Person person = humanManager.findPerson(personId).orElseThrow(IllegalArgumentException::new);

        model.addAttribute("editPersonForm", new PersonToEditPersonFormConverter().convertToEditPersonForm(person));
        model.addAttribute("addQualificationForm", new AddQualificationForm());

        model.addAttribute("person", person);

        if (humanManager.activistManager().isActivist(person)) {
            Activist activist = humanManager.activistManager().findActivistByPerson(person);
            model.addAttribute("isActivist", activist.isActive());
            model.addAttribute("activist", activist);
            model.addAttribute("hasJuleica", activist.hasJuleica());
            model.addAttribute("editActivistForm", new ActivistToEditActivistFormConverter().convertToEditActivistForm(activist));
        } else {
            model.addAttribute("editActivistForm", new EditActivistForm());
        }

        if (humanManager.referentManager().isReferent(person)) {
            model.addAttribute("isReferent", true);
            model.addAttribute("referent", humanManager.referentManager().findReferentByPerson(person));
            model.addAttribute("qualifications", humanManager.referentManager().findReferentByPerson(person).getQualifications());
        }

        return "personDetails";
    }

    /**
     * Updates a person
     * @param personId the id of the person to edit
     * @param personForm person object created by the model
     * @param redirAttr attributes for the view to display some result information
     * @return the person's detail view
     */
    @RequestMapping("/persons/{pid}/edit")
    public String editPerson(@PathVariable("pid") String personId, @ModelAttribute("editPersonForm") EditPersonForm personForm, RedirectAttributes redirAttr) {

        Person person = humanManager.findPerson(personId).orElseThrow(IllegalArgumentException::new);

        humanManager.personManager().updatePerson(person.getId(), editPersonFormDataExtractor.extractPerson(personForm));

        redirAttr.addFlashAttribute("personUpdated", true);
        return "redirect:/persons/" + personId;
    }

    @RequestMapping("/persons/{pid}/edit-activist")
    public String editActivist(@PathVariable("pid") String personId, @ModelAttribute("editActivistForm") EditActivistForm activistForm,
                               RedirectAttributes redirAttr) {

        Person person = humanManager.findPerson(personId).orElseThrow(IllegalArgumentException::new);

        if (activistForm.isActivist()) {
            Activist activist = humanManager.activistManager().createActivistIfNotExists(person);
            if (activistForm.isOwningJuleica()) {
                activist.setJuleicaExpiryDate(LocalDate.parse(activistForm.getJuleicaExpiryDate(),
                        DateTimeFormatter.ofPattern(EditActivistForm.DATE_FORMAT, Locale.GERMAN)));
            } else {
                activist.setJuleicaExpiryDate(null);
            }
            humanManager.activistManager().updateActivist(person.getId(), activist);
        } else {
            humanManager.activistManager().deactivateActivistIfExists(person);
        }

        return "redirect:/persons/" + personId;
    }

    /**
     * Adds a new qualification to a referent. If the person is not a referent already, it will be turned into one.
     * @param personId the id of the person/referent
     * @param qualificationForm qualification object created by the model
     * @param redirAttr attributes for the view to display some result information
     * @return the person's detail view
     */
    @RequestMapping("/persons/{pid}/add-qualification")
    public String addQualification(@PathVariable("pid") String personId, @ModelAttribute("addQualificationForm") AddQualificationForm qualificationForm,
                                   RedirectAttributes redirAttr) {

        Person person = humanManager.findPerson(personId).orElseThrow(IllegalArgumentException::new);
        Referent referent = humanManager.referentManager().createReferentIfNotExists(person);

        Qualification qualification;
        if (AddQualificationForm.AddType.valueOf(qualificationForm.getAddType()) == AddQualificationForm.AddType.NEW) {
            qualification = qualificationManager.createQualification(qualificationForm.getName(), qualificationForm.getDescription());
        } else {
            Optional<Qualification> qualificationWrapper = qualificationManager.findQualification(qualificationForm.getQualification());
            if (qualificationWrapper.isPresent()) {
                qualification = qualificationWrapper.get();
            } else {
                throw new IllegalArgumentException("No qualification exists for name: " + qualificationForm.getQualification());
            }
        }
        referent.addQualification(qualification);
        humanManager.referentManager().saveReferent(referent);

        redirAttr.addFlashAttribute("qualificationAdded", true);
        return "redirect:/persons/" + personId;
    }

    /**
     * Removes a qualification from a referent
     * @param personId the id of the person/referent
     * @param qualificationName qualification object created by the model
     * @param redirAttr attributes for the view to display some result information
     * @return the person's detail view
     */
    @RequestMapping("/persons/{pid}/qualifications/remove")
    public String removeQualification(@PathVariable("pid") String personId, @RequestParam("name") String qualificationName, RedirectAttributes redirAttr) {

        Person person = humanManager.findPerson(personId).orElseThrow(IllegalArgumentException::new);
        Referent referent = humanManager.findReferent(person);

        Qualification qualification = qualificationManager.findQualification(qualificationName).orElseThrow(IllegalArgumentException::new);

        referent.removeQualification(qualification);
        humanManager.referentManager().updateReferent(person.getId(), referent);

        return "redirect:/persons/" + personId;
    }

}
