package seedu.address.model;

import java.util.ArrayList;
import java.util.function.Predicate;

import javafx.collections.ObservableList;

import seedu.address.logic.commands.exceptions.CommandException;

import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluate to true */
    Predicate<ReadOnlyPerson> PREDICATE_SHOW_ALL_PERSONS = unused -> true;

    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyAddressBook newData);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /** Deletes the given person. */
    void deletePerson(ReadOnlyPerson target) throws PersonNotFoundException;

    /** Adds the given person */
    void addPerson(ReadOnlyPerson person) throws DuplicatePersonException;

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     *
     * @throws DuplicatePersonException if updating the person's details causes the person to be equivalent to
     *      another existing person in the list.
     * @throws PersonNotFoundException if {@code target} could not be found in the list.
     */
    void updatePerson(ReadOnlyPerson target, ReadOnlyPerson editedPerson)
            throws DuplicatePersonException, PersonNotFoundException;

    void deleteTag(Tag tag) throws PersonNotFoundException, DuplicatePersonException;

    /** Returns an unmodifiable view of the filtered person list */
    ObservableList<ReadOnlyPerson> getFilteredPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<ReadOnlyPerson> predicate);

    /**
     * Checks if list is empty
     * Returns true if is empty
     */
    Boolean checkIfListEmpty(ArrayList<ReadOnlyPerson> contactList);

    /**
     * Sort contact list in alphabetical order
     * @throws NullPointerException if {@code contactList} is null.
     */
    void sortListByName(ArrayList<ReadOnlyPerson> contactList)  throws CommandException;

    /**
     * @param contactList
     * @throws CommandException
     */
    void sortListByBirthday(ArrayList<ReadOnlyPerson> contactList)  throws CommandException;

    /**
     * @param contactList
     * @throws CommandException
     */
    void sortListByAge(ArrayList<ReadOnlyPerson> contactList)  throws CommandException;

}
