package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.AddressBookChangedEvent;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;

/**
 * Represents the in-memory model of the address book data.
 * All changes to any model should be synchronized.
 */
public class ModelManager extends ComponentManager implements Model {

    public static final String MESSAGE_DUPLICATE_PERSON =  "Duplicate persons in AddressBook.";
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final FilteredList<ReadOnlyPerson> filteredPersons;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, UserPrefs userPrefs) {
        super();
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    @Override
    public void resetData(ReadOnlyAddressBook newData) {
        addressBook.resetData(newData);
        indicateAddressBookChanged();
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    /** Raises an event to indicate the model has changed */
    private void indicateAddressBookChanged() {
        raise(new AddressBookChangedEvent(addressBook));
    }

    @Override
    public synchronized void deletePerson(ReadOnlyPerson target) throws PersonNotFoundException {
        addressBook.removePerson(target);
        indicateAddressBookChanged();
    }

    @Override
    public synchronized void addPerson(ReadOnlyPerson person) throws DuplicatePersonException {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        indicateAddressBookChanged();
    }

    @Override
    public void updatePerson(ReadOnlyPerson target, ReadOnlyPerson editedPerson)
            throws DuplicatePersonException, PersonNotFoundException {
        requireAllNonNull(target, editedPerson);
        addressBook.updatePerson(target, editedPerson);
        indicateAddressBookChanged();
    }

    @Override
    public void deleteTag(Tag tag) throws PersonNotFoundException, DuplicatePersonException  {
        for (int i = 0; i < addressBook.getPersonList().size(); i++) {
            ReadOnlyPerson oldPerson = addressBook.getPersonList().get(i);

            Person newPerson = new Person(oldPerson);
            Set<Tag> newTags = newPerson.getTags();
            newTags.remove(tag);
            newPerson.setTags(newTags);

            addressBook.updatePerson(oldPerson, newPerson);
        }
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code ReadOnlyPerson} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<ReadOnlyPerson> getFilteredPersonList() {
        return FXCollections.unmodifiableObservableList(filteredPersons);
    }

    @Override
    public void updateFilteredPersonList(Predicate<ReadOnlyPerson> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return addressBook.equals(other.addressBook)
                && filteredPersons.equals(other.filteredPersons);
    }

    @Override
    public Boolean checkIfListEmpty(ArrayList<ReadOnlyPerson> contactList) {
        if (filteredPersons.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * @param contactList
     * @throws CommandException
     */
    public void sortListByName(ArrayList<ReadOnlyPerson> contactList) throws CommandException {
        contactList.addAll(filteredPersons);
        Collections.sort(contactList, Comparator.comparing(p -> p.toString().toLowerCase()));

        try {
            addressBook.setPersons(contactList);
            indicateAddressBookChanged();
        } catch (DuplicatePersonException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }
    }

    /**
     * @param contactList
     * @throws CommandException
     */
    public void sortListByAge(ArrayList<ReadOnlyPerson> contactList) throws CommandException {
        contactList.addAll(filteredPersons);
        Collections.sort(contactList, COMPARE_BY_AGE);

        try {
            addressBook.setPersons(contactList);
            indicateAddressBookChanged();
        } catch (DuplicatePersonException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }
    }

    Comparator<ReadOnlyPerson> COMPARE_BY_AGE = new Comparator<ReadOnlyPerson>() {
        public int compare(ReadOnlyPerson firstNum, ReadOnlyPerson secondNum) {
            String newFirstNum = getNewStringAgeFormat(firstNum);
            String newSecondNum = getNewStringAgeFormat(secondNum);
            if (newFirstNum.equals("") || newSecondNum.equals("")) {
                return newSecondNum.compareTo(newFirstNum);
            } else {
                return newFirstNum.compareTo(newSecondNum);
            }
        }
    };

    static String getNewStringAgeFormat(ReadOnlyPerson person) {
        if (person.getBirthday().toString().equals("")) {
            return "";
        } else {
            String numInString = person.getBirthday().toString();    // Converts birthday to String type
            String dayForNum = numInString.substring(0, 2);        // Index of day in dd/mm/yyyy
            String monthForNum = numInString.substring(3, 5);      // Index of month in dd/mm/yyyy
            String yearForNum = numInString.substring(6, 10);      // Index of year in dd/mm/yyyy
            return yearForNum + monthForNum + dayForNum;           // Return string format yyyymmdd
        }
    }


    /**
     * @param contactList
     * @throws CommandException
     */
    public void sortListByBirthday(ArrayList<ReadOnlyPerson> contactList) throws CommandException {
        contactList.addAll(filteredPersons);
        Collections.sort(contactList, COMPARE_BY_BIRTHDAY);

        try {
            addressBook.setPersons(contactList);
            indicateAddressBookChanged();
        } catch (DuplicatePersonException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }
    }

    Comparator<ReadOnlyPerson> COMPARE_BY_BIRTHDAY = new Comparator<ReadOnlyPerson>() {
        public int compare(ReadOnlyPerson firstPerson, ReadOnlyPerson secondPerson) {
            String newFirstNum = getNewStringBirthdayFormat(firstPerson);
            String newSecondNum = getNewStringBirthdayFormat(secondPerson);
            if (newFirstNum.equals("") || newSecondNum.equals("")) {
                return newSecondNum.compareTo(newFirstNum);
            } else {
                return newFirstNum.compareTo(newSecondNum);
            }
        }
    };

    static String getNewStringBirthdayFormat(ReadOnlyPerson person) {
        if (person.getBirthday().toString().equals("")) {
            return "";
        } else {
            String numInString = person.getBirthday().toString();  // Converts birthday to String type
            String dayForNum = numInString.substring(0, 2);        // Index of day in dd/mm/yyyy
            String monthForNum = numInString.substring(3, 5);      // Index of month in dd/mm/yyyy
            String yearForNum = numInString.substring(6, 10);      // Index of year in dd/mm/yyyy
            return monthForNum + dayForNum + yearForNum;           // Return String format mmddyyy
        }
    }
}
