# duyson98
###### \java\guitests\guihandles\ReminderCardHandle.java
``` java

package guitests.guihandles;

import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;

/**
 * Provides a handle to a reminder card in the reminder list panel.
 */
public class ReminderCardHandle extends NodeHandle<Node> {
    private static final String ID_FIELD_ID = "#id";
    private static final String TASK_FIELD_ID = "#task";
    private static final String PRIORITY_FIELD_ID = "#priority";
    private static final String DATE_FIELD_ID = "#datentime";
    private static final String MESSAGE_FIELD_ID = "#message";
    private static final String TAGS_FIELD_ID = "#tags";

    private final Label idLabel;
    private final Label taskLabel;
    private final Label priorityLabel;
    private final Label dateLabel;
    private final Label messageLabel;
    private final List<Label> tagLabels;

    public ReminderCardHandle(Node cardNode) {
        super(cardNode);

        this.idLabel = getChildNode(ID_FIELD_ID);
        this.taskLabel = getChildNode(TASK_FIELD_ID);
        this.priorityLabel = getChildNode(PRIORITY_FIELD_ID);
        this.dateLabel = getChildNode(DATE_FIELD_ID);
        this.messageLabel = getChildNode(MESSAGE_FIELD_ID);

        Region tagsContainer = getChildNode(TAGS_FIELD_ID);
        this.tagLabels = tagsContainer
                .getChildrenUnmodifiable()
                .stream()
                .map(Label.class::cast)
                .collect(Collectors.toList());
    }

    public String getId() {
        return idLabel.getText();
    }

    public String getTask() {
        return taskLabel.getText();
    }

    public String getPriority() {
        return priorityLabel.getText();
    }

    public String getDate() {
        return dateLabel.getText();
    }

    public String getMessage() {
        return messageLabel.getText();
    }

    public List<String> getTags() {
        return tagLabels
                .stream()
                .map(Label::getText)
                .collect(Collectors.toList());
    }
}
```
###### \java\guitests\guihandles\ReminderListPanelHandle.java
``` java

package guitests.guihandles;

import java.util.List;
import java.util.Optional;

import javafx.scene.control.ListView;
import seedu.address.model.reminder.ReadOnlyReminder;
import seedu.address.ui.ReminderCard;

/**
 * Provides a handle for {@code ReminderListPanel} containing the list of {@code ReminderCard}.
 */
public class ReminderListPanelHandle extends NodeHandle<ListView<ReminderCard>> {
    public static final String REMINDER_LIST_VIEW_ID = "#reminderListView";

    private Optional<ReminderCard> lastRememberedSelectedReminderCard;

    public ReminderListPanelHandle(ListView<ReminderCard> reminderListPanelNode) {
        super(reminderListPanelNode);
    }

    /**
     * Returns a handle to the selected {@code ReminderCardHandle}.
     * A maximum of 1 item can be selected at any time.
     * @throws AssertionError if no card is selected, or more than 1 card is selected.
     */
    public ReminderCardHandle getHandleToSelectedCard() {
        List<ReminderCard> reminderList = getRootNode().getSelectionModel().getSelectedItems();

        if (reminderList.size() != 1) {
            throw new AssertionError("Reminder list size expected 1.");
        }

        return new ReminderCardHandle(reminderList.get(0).getRoot());
    }

    /**
     * Returns the index of the selected card.
     */
    public int getSelectedCardIndex() {
        return getRootNode().getSelectionModel().getSelectedIndex();
    }

    /**
     * Returns true if a card is currently selected.
     */
    public boolean isAnyCardSelected() {
        List<ReminderCard> selectedCardsList = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedCardsList.size() > 1) {
            throw new AssertionError("Card list size expected 0 or 1.");
        }

        return !selectedCardsList.isEmpty();
    }

    /**
     * Navigates the listview to display and select the reminder.
     */
    public void navigateToCard(ReadOnlyReminder reminder) {
        List<ReminderCard> cards = getRootNode().getItems();
        Optional<ReminderCard> matchingCard = cards.stream().filter(card -> card.reminder.equals(reminder)).findFirst();

        if (!matchingCard.isPresent()) {
            throw new IllegalArgumentException("Reminder does not exist.");
        }

        guiRobot.interact(() -> {
            getRootNode().scrollTo(matchingCard.get());
            getRootNode().getSelectionModel().select(matchingCard.get());
        });
        guiRobot.pauseForHuman();
    }

    /**
     * Returns the reminder card handle of a reminder associated with the {@code index} in the list.
     */
    public ReminderCardHandle getReminderCardHandle(int index) {
        return getReminderCardHandle(getRootNode().getItems().get(index).reminder);
    }

    /**
     * Returns the {@code ReminderCardHandle} of the specified {@code reminder} in the list.
     */
    public ReminderCardHandle getReminderCardHandle(ReadOnlyReminder reminder) {
        Optional<ReminderCardHandle> handle = getRootNode().getItems().stream()
                .filter(card -> card.reminder.equals(reminder))
                .map(card -> new ReminderCardHandle(card.getRoot()))
                .findFirst();
        return handle.orElseThrow(() -> new IllegalArgumentException("Reminder does not exist."));
    }

    /**
     * Selects the {@code ReminderCard} at {@code index} in the list.
     */
    public void select(int index) {
        getRootNode().getSelectionModel().select(index);
    }

    /**
     * Remembers the selected {@code ReminderCard} in the list.
     */
    public void rememberSelectedReminderCard() {
        List<ReminderCard> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            lastRememberedSelectedReminderCard = Optional.empty();
        } else {
            lastRememberedSelectedReminderCard = Optional.of(selectedItems.get(0));
        }
    }

    /**
     * Returns true if the selected {@code ReminderCard} is different from the value remembered by the most recent
     * {@code rememberSelectedReminderCard()} call.
     */
    public boolean isSelectedReminderCardChanged() {
        List<ReminderCard> selectedItems = getRootNode().getSelectionModel().getSelectedItems();

        if (selectedItems.size() == 0) {
            return lastRememberedSelectedReminderCard.isPresent();
        } else {
            return !lastRememberedSelectedReminderCard.isPresent()
                    || !lastRememberedSelectedReminderCard.get().equals(selectedItems.get(0));
        }
    }

    /**
     * Returns the size of the list.
     */
    public int getListSize() {
        return getRootNode().getItems().size();
    }
}
```
###### \java\seedu\address\logic\commands\AddReminderCommandIntegrationTest.java
``` java

package seedu.address.logic.commands;

import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalAccounts.getTypicalDatabase;
import static seedu.address.testutil.TypicalReminders.getTypicalAddressBook;

import org.junit.Before;
import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.reminder.Reminder;
import seedu.address.testutil.ReminderBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code AddReminderCommand}.
 */
public class AddReminderCommandIntegrationTest {

    private Model model;

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), getTypicalDatabase(), new UserPrefs());
    }

    @Test
    public void execute_newReminder_success() throws Exception {
        Reminder validReminder = new ReminderBuilder().build();

        Model expectedModel = new ModelManager(model.getAddressBook(), model.getDatabase(), new UserPrefs());
        expectedModel.addReminder(validReminder);

        assertCommandSuccess(prepareCommand(validReminder, model), model,
                String.format(AddReminderCommand.MESSAGE_SUCCESS, validReminder), expectedModel);
    }

    @Test
    public void execute_duplicateReminder_throwsCommandException() {
        Reminder reminderInList = new Reminder(model.getAddressBook().getReminderList().get(0));
        assertCommandFailure(prepareCommand(reminderInList, model), model,
                AddReminderCommand.MESSAGE_DUPLICATE_REMINDER);
    }

    /**
     * Generates a new {@code AddReminderCommand} which upon execution, adds {@code reminder} into the {@code model}.
     */
    private AddReminderCommand prepareCommand(Reminder reminder, Model model) {
        AddReminderCommand command = new AddReminderCommand(reminder);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }
}
```
###### \java\seedu\address\logic\commands\AddReminderCommandTest.java
``` java

package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javafx.collections.ObservableList;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.ReadOnlyDatabase;
import seedu.address.model.account.ReadOnlyAccount;
import seedu.address.model.account.exceptions.DuplicateAccountException;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.reminder.ReadOnlyReminder;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.reminder.exceptions.DuplicateReminderException;
import seedu.address.model.reminder.exceptions.ReminderNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.ReminderBuilder;

public class AddReminderCommandTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void constructor_nullReminder_throwsNullPointerException() {
        thrown.expect(NullPointerException.class);
        new AddReminderCommand(null);
    }

    @Test
    public void execute_reminderAcceptedByModel_addSuccessful() throws Exception {
        ModelStubAcceptingReminderAdded modelStub = new ModelStubAcceptingReminderAdded();
        Reminder validReminder = new ReminderBuilder().build();

        CommandResult commandResult = getAddReminderCommandForReminder(validReminder, modelStub).execute();

        assertEquals(String.format(AddReminderCommand.MESSAGE_SUCCESS, validReminder), commandResult.feedbackToUser);
        assertEquals(Arrays.asList(validReminder), modelStub.remindersAdded);
    }

    @Test
    public void execute_duplicateReminder_throwsCommandException() throws Exception {
        ModelStub modelStub = new ModelStubThrowingDuplicateReminderException();
        Reminder validReminder = new ReminderBuilder().build();

        thrown.expect(CommandException.class);
        thrown.expectMessage(AddReminderCommand.MESSAGE_DUPLICATE_REMINDER);

        getAddReminderCommandForReminder(validReminder, modelStub).execute();
    }

    @Test
    public void equals() {
        Reminder reminder = new ReminderBuilder().withTask("Project").build();
        Reminder anotherReminder = new ReminderBuilder().withTask("Assignment").build();
        AddReminderCommand command = new AddReminderCommand(reminder);
        AddReminderCommand anotherCommand = new AddReminderCommand(anotherReminder);

        // same object -> returns true
        assertTrue(command.equals(command));

        // same values -> returns true
        AddReminderCommand commandCopy = new AddReminderCommand(reminder);
        assertTrue(command.equals(commandCopy));

        // different types -> returns false
        assertFalse(command.equals(1));

        // null -> returns false
        assertFalse(command.equals(null));

        // different reminder -> returns false
        assertFalse(command.equals(anotherCommand));
    }

    /**
     * Generates a new AddReminderCommand with the details of the given reminder.
     */
    private AddReminderCommand getAddReminderCommandForReminder(Reminder reminder, Model model) {
        AddReminderCommand command = new AddReminderCommand(reminder);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        @Override
        public void addPerson(ReadOnlyPerson person) throws DuplicatePersonException {
            fail("This method should not be called.");
        }

        @Override
        public boolean checkAccount(ReadOnlyAccount account) {
            fail("This method should not be called");
            return true;
        }

        @Override
        public void addAccount(ReadOnlyAccount account) throws DuplicateAccountException {
            fail("This method should not be called");
        }
        @Override
        public void addReminder(ReadOnlyReminder newData) throws DuplicateReminderException {
            fail("This method should not be called.");
        }

        @Override
        public void resetData(ReadOnlyAddressBook newData) {
            fail("This method should not be called.");
        }

        @Override
        public void resetDatabase(ReadOnlyDatabase newData) {
            fail("This method should not be called.");
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public ReadOnlyDatabase getDatabase() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public void deletePerson(ReadOnlyPerson target) throws PersonNotFoundException {
            fail("This method should not be called.");
        }

        @Override
        public void deleteAccount(ReadOnlyAccount account) throws PersonNotFoundException {
            fail("This method should not be called.");
        }

        @Override
        public void deleteReminder(ReadOnlyReminder target) throws ReminderNotFoundException {
            fail("This method should not be called.");
        }

        @Override
        public void updatePerson(ReadOnlyPerson target, ReadOnlyPerson editedPerson)
                throws DuplicatePersonException {
            fail("This method should not be called.");
        }

        public void updateAccount(ReadOnlyAccount account, ReadOnlyAccount editedAccount)
                throws DuplicateAccountException {
            fail("This method should not be called.");
        }


        @Override
        public void updateReminder(ReadOnlyReminder target, ReadOnlyReminder editedReminder)
                throws DuplicateReminderException {
            fail("This method should not be called.");
        }

        @Override
        public void deleteUnusedTag(Tag tag) {
            fail("This method should not be called.");
        }

        @Override
        public ObservableList<ReadOnlyPerson> getFilteredPersonList() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public ObservableList<ReadOnlyAccount> getFilteredAccountList() {
            fail("This method should not be called.");
            return null;
        }


        @Override
        public ObservableList<ReadOnlyReminder> getFilteredReminderList() {
            fail("This method should not be called.");
            return null;
        }

        @Override
        public void updateFilteredPersonList(Predicate<ReadOnlyPerson> predicate) {
            fail("This method should not be called.");
        }

        @Override
        public void updateFilteredAccountList(Predicate<ReadOnlyAccount> predicate) {
            fail("This method should not be called.");
        }

        @Override
        public void updateFilteredReminderList(Predicate<ReadOnlyReminder> predicate) {
            fail("This method should not be called.");
        }

        @Override
        public void deletePersonTag(Tag tag) {
            fail("This metthod should not be called.");
        }

        @Override
        public void deleteReminderTag(Tag tag) {
            fail("This metthod should not be called.");
        }

        public Boolean checkIfPersonListEmpty(ArrayList<ReadOnlyPerson> contactList) {
            fail("This method should not be called.");
            return false;
        }

        public Boolean checkIfReminderListEmpty(ArrayList<ReadOnlyReminder> reminderList) {
            fail("This method should not be called.");
            return false;
        }

        @Override
        public void sortListByName(ArrayList<ReadOnlyPerson> contactList) throws CommandException {
            fail("This method should not be called.");
        }

        @Override
        public void sortListByBirthday(ArrayList<ReadOnlyPerson> contactList) throws CommandException {
            fail("This method should not be called.");
        }

        @Override
        public void sortListByAge(ArrayList<ReadOnlyPerson> contactList) throws CommandException {
            fail("This method should not be called.");
        }

        @Override
        public void sortListByPriority(ArrayList<ReadOnlyReminder> contactList)  throws CommandException {
            fail("This method should not be called.");
        }
    }

    /**
     * A Model stub that always throw a DuplicateReminderException when trying to add a reminder.
     */
    private class ModelStubThrowingDuplicateReminderException extends ModelStub {
        @Override
        public void addReminder(ReadOnlyReminder reminder) throws DuplicateReminderException {
            throw new DuplicateReminderException();
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }

    /**
     * A Model stub that always accept the reminder being added.
     */
    private class ModelStubAcceptingReminderAdded extends ModelStub {
        final ArrayList<Reminder> remindersAdded = new ArrayList<>();

        @Override
        public void addReminder(ReadOnlyReminder reminder) throws DuplicateReminderException {
            remindersAdded.add(new Reminder(reminder));
        }

        @Override
        public ReadOnlyAddressBook getAddressBook() {
            return new AddressBook();
        }
    }
}
```
###### \java\seedu\address\logic\commands\DeleteReminderCommandTest.java
``` java

package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFirstReminderOnly;
import static seedu.address.testutil.TypicalAccounts.getTypicalDatabase;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_REMINDER;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_REMINDER;
import static seedu.address.testutil.TypicalReminders.getTypicalAddressBook;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.reminder.ReadOnlyReminder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code DeleteReminderCommand}.
 */
public class DeleteReminderCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), getTypicalDatabase(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() throws Exception {
        ReadOnlyReminder reminderToDelete = model.getFilteredReminderList().get(INDEX_FIRST_REMINDER.getZeroBased());
        DeleteReminderCommand command = prepareCommand(INDEX_FIRST_REMINDER);

        String expectedMessage = String.format(DeleteReminderCommand.MESSAGE_DELETE_REMINDER_SUCCESS, reminderToDelete);

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), model.getDatabase(), new UserPrefs());
        expectedModel.deleteReminder(reminderToDelete);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() throws Exception {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredReminderList().size() + 1);
        DeleteReminderCommand command = prepareCommand(outOfBoundIndex);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_REMINDER_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() throws Exception {
        showFirstReminderOnly(model);

        ReadOnlyReminder reminderToDelete = model.getFilteredReminderList().get(INDEX_FIRST_REMINDER.getZeroBased());
        DeleteReminderCommand command = prepareCommand(INDEX_FIRST_REMINDER);

        String expectedMessage = String.format(DeleteReminderCommand.MESSAGE_DELETE_REMINDER_SUCCESS, reminderToDelete);

        Model expectedModel = new ModelManager(model.getAddressBook(), model.getDatabase(), new UserPrefs());
        expectedModel.deleteReminder(reminderToDelete);
        showNoReminder(expectedModel);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showFirstReminderOnly(model);

        Index outOfBoundIndex = INDEX_SECOND_REMINDER;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getReminderList().size());

        DeleteReminderCommand command = prepareCommand(outOfBoundIndex);

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_REMINDER_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteReminderCommand command = new DeleteReminderCommand(INDEX_FIRST_REMINDER);

        // same object -> returns true
        assertTrue(command.equals(command));

        // same values -> returns true
        DeleteReminderCommand commandCopy = new DeleteReminderCommand(INDEX_FIRST_REMINDER);
        assertTrue(command.equals(commandCopy));

        // different types -> returns false
        assertFalse(command.equals(new ClearCommand()));

        // null -> returns false
        assertFalse(command.equals(null));

        // different reminder -> returns false
        DeleteReminderCommand anotherCommand = new DeleteReminderCommand(INDEX_SECOND_REMINDER);
        assertFalse(command.equals(anotherCommand));
    }

    /**
     * Returns a {@code DeleteReminderCommand} with the parameter {@code index}.
     */
    private DeleteReminderCommand prepareCommand(Index index) {
        DeleteReminderCommand command = new DeleteReminderCommand(index);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

    /**
     * Updates {@code model}'s filtered list to show no reminder.
     */
    private void showNoReminder(Model model) {
        model.updateFilteredReminderList(r -> false);

        assert model.getFilteredReminderList().isEmpty();
    }
}
```
###### \java\seedu\address\logic\commands\RetagCommandTest.java
``` java

package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.RetagCommand.MESSAGE_SUCCESS;
import static seedu.address.logic.commands.RetagCommand.MESSAGE_TAG_NOT_FOUND;
import static seedu.address.testutil.TypicalAccounts.getTypicalDatabase;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

public class RetagCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), getTypicalDatabase(), new UserPrefs());

    @Test
    public void execute_success() throws Exception {
        Tag targetTag = new Tag("friends");
        Tag newTag = new Tag("enemies");
        RetagCommand command = prepareCommand(targetTag, newTag);

        String expectedMessage = String.format(MESSAGE_SUCCESS, targetTag.toString(), newTag.toString());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());
        for (ReadOnlyPerson person : expectedModel.getFilteredPersonList()) {
            Person retaggedPerson = new Person(person);
            UniqueTagList updatedTags = new UniqueTagList(retaggedPerson.getTags());
            if (updatedTags.contains(targetTag)) {
                updatedTags.remove(targetTag);
                updatedTags.add(newTag);
            }
            retaggedPerson.setTags(updatedTags.toSet());
            expectedModel.updatePerson(person, retaggedPerson);
        }
        expectedModel.deleteUnusedTag(targetTag);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_tagNotFound() throws Exception {
        Tag targetTag = new Tag("enemies");
        Tag newTag = new Tag("friends");
        RetagCommand command = prepareCommand(targetTag, newTag);

        String expectedMessage = String.format(MESSAGE_TAG_NOT_FOUND, targetTag.toString());

        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void equals() throws Exception {
        Tag targetTag = new Tag("enemies");
        Tag newTag = new Tag("friends");
        RetagCommand command = new RetagCommand(targetTag, newTag);

        // same value -> returns true
        assertTrue(command.equals(new RetagCommand(targetTag, newTag)));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null -> returns false
        assertFalse(command.equals(null));

        // different type -> returns false
        assertFalse(command.equals(new ClearCommand()));

        // different tag name -> returns false
        Tag anotherTag = new Tag("partners");
        assertFalse(command.equals(new RetagCommand(targetTag, anotherTag)));
    }

    /**
     * Parses {@code userInput} into a {@code RetagCommand}.
     */
    private RetagCommand prepareCommand(Tag targetTag, Tag newTag) throws Exception {
        RetagCommand command = new RetagCommand(targetTag, newTag);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

}
```
###### \java\seedu\address\logic\commands\RetrieveCommandTest.java
``` java

package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.commons.core.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.RetrieveCommand.MESSAGE_NOT_FOUND;
import static seedu.address.testutil.TypicalAccounts.getTypicalDatabase;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import org.junit.Test;

import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagContainsKeywordPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code RetrieveCommand}.
 */
public class RetrieveCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), getTypicalDatabase(), new UserPrefs());

    @Test
    public void equals() throws Exception {
        TagContainsKeywordPredicate predicate = new TagContainsKeywordPredicate(new Tag("friends"));
        RetrieveCommand command = new RetrieveCommand(predicate);

        // same value -> returns true
        assertTrue(command.equals(new RetrieveCommand(predicate)));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null -> returns false
        assertFalse(command.equals(null));

        // different type -> returns false
        assertFalse(command.equals(new ClearCommand()));

        // different tag name -> returns false
        assertFalse(command.equals(new RetrieveCommand(new TagContainsKeywordPredicate(new Tag("family")))));
    }

    @Test
    public void execute_noPersonFound() throws Exception {
        StringJoiner joiner = new StringJoiner(", ");
        for (Tag tag: model.getAddressBook().getTagList()) {
            joiner.add(tag.toString());
        }
        String expectedMessage = String.format(MESSAGE_NOT_FOUND, joiner.toString());
        RetrieveCommand command = prepareCommand("thisTag");
        assertCommandSuccess(command, expectedMessage, Collections.emptyList());
    }

    @Test
    public void execute_multiplePersonsFound() throws Exception {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2);
        RetrieveCommand command = prepareCommand("retrieveTester");
        assertCommandSuccess(command, expectedMessage, Arrays.asList(ALICE, BENSON));
    }

    /**
     * Parses {@code userInput} into a {@code RetrieveCommand}.
     */
    private RetrieveCommand prepareCommand(String userInput) throws Exception {
        if (userInput.isEmpty()) {
            RetrieveCommand command = new RetrieveCommand(new TagContainsKeywordPredicate(new Tag(userInput)));
        }
        RetrieveCommand command = new RetrieveCommand(new TagContainsKeywordPredicate(new Tag(userInput)));
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

    /**
     * Asserts that {@code command} is successfully executed, and<br>
     *     - the command feedback is equal to {@code expectedMessage}<br>
     *     - the {@code FilteredList<ReadOnlyPerson>} is equal to {@code expectedList}<br>
     *     - the {@code AddressBook} in model remains the same after executing the {@code command}
     */
    public void assertCommandSuccess(RetrieveCommand command, String expectedMessage,
                                     List<ReadOnlyPerson> expectedList) {
        AddressBook expectedAddressBook = new AddressBook(model.getAddressBook());
        CommandResult commandResult = command.execute();

        assertEquals(expectedMessage, commandResult.feedbackToUser);
        assertEquals(expectedList, model.getFilteredPersonList());
        assertEquals(expectedAddressBook, model.getAddressBook());
    }

}
```
###### \java\seedu\address\logic\commands\TagCommandTest.java
``` java

package seedu.address.logic.commands;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFirstAndSecondPersonsOnly;
import static seedu.address.logic.commands.TagCommand.MESSAGE_INVALID_INDEXES;
import static seedu.address.testutil.TypicalAccounts.getTypicalDatabase;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class TagCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), getTypicalDatabase(), new UserPrefs());

    @Test
    public void execute_unfilteredList_success() throws Exception {
        PersonBuilder firstPersonInList = new PersonBuilder(ALICE);
        Person firstTaggedPerson = firstPersonInList.withTags("friends", "retrieveTester", "tagTester").build();
        PersonBuilder secondPersonInList = new PersonBuilder(BENSON);
        Person secondTaggedPerson = secondPersonInList.withTags("owesMoney", "friends",
                "retrieveTester", "tagTester").build();
        Tag tag = new Tag("tagTester");
        TagCommand command = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), tag);

        String expectedMessage = String.format(TagCommand.MESSAGE_SUCCESS, 2, tag.toString()) + " "
                + firstTaggedPerson.getName().toString() + ", "
                + secondTaggedPerson.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());
        expectedModel.updatePerson(model.getFilteredPersonList().get(0), firstTaggedPerson);
        expectedModel.updatePerson(model.getFilteredPersonList().get(1), secondTaggedPerson);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unfilteredListContainsPersonsWithTag_success() throws Exception {
        PersonBuilder firstPersonInList = new PersonBuilder(ALICE);
        Person firstTaggedPerson = firstPersonInList.withTags("friends", "retrieveTester", "owesMoney").build();
        PersonBuilder secondPersonInList = new PersonBuilder(BENSON);
        Person secondTaggedPerson = secondPersonInList.withTags("owesMoney", "friends", "retrieveTester").build();
        Tag tag = new Tag("owesMoney");
        TagCommand command = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), tag);

        String expectedMessage = String.format(TagCommand.MESSAGE_SUCCESS, 1, tag.toString()) + " "
                + firstTaggedPerson.getName().toString() + "\n"
                + String.format(TagCommand.MESSAGE_PERSONS_ALREADY_HAVE_TAG, 1) + " "
                + secondTaggedPerson.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());
        expectedModel.updatePerson(model.getFilteredPersonList().get(0), firstTaggedPerson);
        expectedModel.updatePerson(model.getFilteredPersonList().get(1), secondTaggedPerson);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() throws Exception {
        showFirstAndSecondPersonsOnly(model);

        ReadOnlyPerson firstPersonInList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person firstTaggedPerson = new PersonBuilder(firstPersonInList).withTags("friends",
                "retrieveTester", "tagTester").build();
        ReadOnlyPerson secondPersonInList = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person secondTaggedPerson = new PersonBuilder(secondPersonInList).withTags("owesMoney", "friends",
                "retrieveTester", "tagTester").build();
        Tag tag = new Tag("tagTester");
        TagCommand command = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), tag);

        String expectedMessage = String.format(TagCommand.MESSAGE_SUCCESS, 2, tag.toString()) + " "
                + firstTaggedPerson.getName().toString() + ", "
                + secondTaggedPerson.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());
        expectedModel.updatePerson(model.getFilteredPersonList().get(0), firstTaggedPerson);
        expectedModel.updatePerson(model.getFilteredPersonList().get(1), secondTaggedPerson);
        showFirstAndSecondPersonsOnly(expectedModel);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredListContainsPersonsWithTag_success() throws Exception {
        showFirstAndSecondPersonsOnly(model);

        ReadOnlyPerson firstPersonInList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person firstTaggedPerson = new PersonBuilder(firstPersonInList).withTags("friends",
                "retrieveTester", "owesMoney").build();
        ReadOnlyPerson secondPersonInList = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person secondTaggedPerson = new PersonBuilder(secondPersonInList).withTags("owesMoney", "friends",
                "retrieveTester").build();
        Tag tag = new Tag("owesMoney");
        TagCommand command = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), tag);

        String expectedMessage = String.format(TagCommand.MESSAGE_SUCCESS, 1, tag.toString()) + " "
                + firstTaggedPerson.getName().toString() + "\n"
                + String.format(TagCommand.MESSAGE_PERSONS_ALREADY_HAVE_TAG, 1) + " "
                + secondTaggedPerson.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());
        expectedModel.updatePerson(model.getFilteredPersonList().get(0), firstTaggedPerson);
        expectedModel.updatePerson(model.getFilteredPersonList().get(1), secondTaggedPerson);
        showFirstAndSecondPersonsOnly(expectedModel);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexesUnfilteredList_failure() throws Exception {
        Index outOfBound = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        TagCommand command = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON, outOfBound), new Tag("tagTester"));

        assertCommandFailure(command, model, MESSAGE_INVALID_INDEXES);
    }

    @Test
    public void execute_invalidPersonIndexesFilteredList_failure() throws Exception {
        showFirstAndSecondPersonsOnly(model);

        Index outOfBoundIndex = INDEX_THIRD_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        TagCommand command = prepareCommand(Arrays.asList(INDEX_FIRST_PERSON, outOfBoundIndex), new Tag("tagTester"));

        assertCommandFailure(command, model, MESSAGE_INVALID_INDEXES);
    }

    @Test
    public void equals() throws Exception {
        final List<Index> indexList = Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        final Tag tag = new Tag("dummyTag");
        final TagCommand command = new TagCommand(indexList, tag);

        // same values -> returns true
        final List<Index> indexListCopy = Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        final Tag tagCopy = new Tag("dummyTag");
        assertTrue(command.equals(new TagCommand(indexListCopy, tagCopy)));

        // same object -> returns true
        assertTrue(command.equals(command));

        // null -> returns false
        assertFalse(command.equals(null));

        // different types -> returns false
        assertFalse(command.equals(new ClearCommand()));

        // different index list -> returns false
        final List<Index> anotherIndexList = Arrays.asList(INDEX_FIRST_PERSON, INDEX_THIRD_PERSON);
        assertFalse(command.equals(new TagCommand(anotherIndexList, tag)));

        // different tag -> returns false
        final Tag anotherTag = new Tag("anotherTag");
        assertFalse(command.equals(new TagCommand(indexList, anotherTag)));
    }

    /**
     * Returns an {@code TagCommand}.
     */
    private TagCommand prepareCommand(List<Index> indexes, Tag tag) {
        TagCommand command = new TagCommand(indexes, tag);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

}
```
###### \java\seedu\address\logic\commands\UntagCommandTest.java
``` java

package seedu.address.logic.commands;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showFirstAndSecondPersonsOnly;
import static seedu.address.logic.commands.UntagCommand.MESSAGE_INVALID_INDEXES;
import static seedu.address.logic.commands.UntagCommand.MESSAGE_SUCCESS_ALL_TAGS;
import static seedu.address.logic.commands.UntagCommand.MESSAGE_SUCCESS_ALL_TAGS_IN_LIST;
import static seedu.address.logic.commands.UntagCommand.MESSAGE_SUCCESS_MULTIPLE_TAGS_IN_LIST;
import static seedu.address.logic.commands.UntagCommand.MESSAGE_TAG_NOT_FOUND;
import static seedu.address.testutil.TypicalAccounts.getTypicalDatabase;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

import org.junit.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;
import seedu.address.testutil.PersonBuilder;

public class UntagCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), getTypicalDatabase(), new UserPrefs());

    @Test
    public void execute_unfilteredList_success() throws Exception {
        PersonBuilder firstPersonInList = new PersonBuilder(ALICE);
        Person firstUntaggedPerson = firstPersonInList.withTags().build();
        PersonBuilder secondPersonInList = new PersonBuilder(BENSON);
        Person secondUntaggedPerson = secondPersonInList.withTags("owesMoney").build();
        Tag firstTag = new Tag("friends");
        Tag secondTag = new Tag("retrieveTester");
        UntagCommand command = prepareCommand(false,
                Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), Arrays.asList(firstTag, secondTag));

        String expectedMessage = String.format(UntagCommand.MESSAGE_SUCCESS, 2,
                firstTag.toString() + ", " + secondTag.toString()) + " "
                + firstUntaggedPerson.getName().toString() + ", " + secondUntaggedPerson.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());
        expectedModel.updatePerson(model.getFilteredPersonList().get(0), firstUntaggedPerson);
        expectedModel.updatePerson(model.getFilteredPersonList().get(1), secondUntaggedPerson);
        expectedModel.deleteUnusedTag(firstTag);
        expectedModel.deleteUnusedTag(secondTag);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_unfilteredListContainsPersonsWithoutTag_success() throws Exception {
        PersonBuilder firstPersonInList = new PersonBuilder(ALICE);
        Person firstUntaggedPerson = firstPersonInList.withTags("friends", "retrieveTester").build();
        PersonBuilder secondPersonInList = new PersonBuilder(BENSON);
        Person secondUntaggedPerson = secondPersonInList.withTags("friends", "retrieveTester").build();
        Tag firstTag = new Tag("owesMoney");
        Tag secondTag = new Tag("retrieveTester");
        UntagCommand command = prepareCommand(false,
                Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), Arrays.asList(firstTag));

        String expectedMessage = String.format(UntagCommand.MESSAGE_SUCCESS, 1, firstTag.toString()) + " "
                + secondUntaggedPerson.getName().toString() + "\n"
                + String.format(UntagCommand.MESSAGE_PERSONS_DO_NOT_HAVE_TAGS, 1) + " "
                + firstUntaggedPerson.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());
        expectedModel.updatePerson(model.getFilteredPersonList().get(0), firstUntaggedPerson);
        expectedModel.updatePerson(model.getFilteredPersonList().get(1), secondUntaggedPerson);
        expectedModel.deleteUnusedTag(firstTag);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() throws Exception {
        showFirstAndSecondPersonsOnly(model);

        ReadOnlyPerson firstPersonInList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person firstUntaggedPerson = new PersonBuilder(firstPersonInList).withTags().build();
        ReadOnlyPerson secondPersonInList = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person secondUntaggedPerson = new PersonBuilder(secondPersonInList).withTags("owesMoney").build();
        Tag firstTag = new Tag("friends");
        Tag secondTag = new Tag("retrieveTester");
        UntagCommand command = prepareCommand(false,
                Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), Arrays.asList(firstTag, secondTag));

        String expectedMessage = String.format(UntagCommand.MESSAGE_SUCCESS, 2,
                firstTag.toString() + ", " + secondTag.toString()) + " "
                + firstUntaggedPerson.getName().toString() + ", " + secondUntaggedPerson.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());
        showFirstAndSecondPersonsOnly(expectedModel);

        expectedModel.updatePerson(model.getFilteredPersonList().get(0), firstUntaggedPerson);
        expectedModel.updatePerson(model.getFilteredPersonList().get(1), secondUntaggedPerson);
        expectedModel.deleteUnusedTag(firstTag);
        expectedModel.deleteUnusedTag(secondTag);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredListContainsPersonsWithoutTag_success() throws Exception {
        showFirstAndSecondPersonsOnly(model);

        ReadOnlyPerson firstPersonInList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person firstUntaggedPerson = new PersonBuilder(firstPersonInList).withTags("friends",
                "retrieveTester").build();
        ReadOnlyPerson secondPersonInList = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person secondUntaggedPerson = new PersonBuilder(secondPersonInList).withTags("friends",
                "retrieveTester").build();
        Tag firstTag = new Tag("owesMoney");
        UntagCommand command = prepareCommand(false,
                Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), Arrays.asList(firstTag));

        String expectedMessage = String.format(UntagCommand.MESSAGE_SUCCESS, 1, firstTag.toString()) + " "
                + secondUntaggedPerson.getName().toString() + "\n"
                + String.format(UntagCommand.MESSAGE_PERSONS_DO_NOT_HAVE_TAGS, 1) + " "
                + firstUntaggedPerson.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());
        showFirstAndSecondPersonsOnly(expectedModel);

        expectedModel.updatePerson(model.getFilteredPersonList().get(0), firstUntaggedPerson);
        expectedModel.updatePerson(model.getFilteredPersonList().get(1), secondUntaggedPerson);
        expectedModel.deleteUnusedTag(firstTag);

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_allTagsInUnfilteredList_success() throws Exception {
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());

        for (ReadOnlyPerson person : model.getFilteredPersonList()) {
            Person untaggedPerson = new PersonBuilder(person).withTags().build();
            expectedModel.updatePerson(person, untaggedPerson);
            for (Tag tag : person.getTags()) {
                expectedModel.deleteUnusedTag(tag);
            }
        }

        UntagCommand command = prepareCommand(true, Collections.emptyList(), Collections.emptyList());

        String expectedMessage = MESSAGE_SUCCESS_ALL_TAGS_IN_LIST;

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_severalTagsInUnfilteredList_success() throws Exception {
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());

        Tag firstTag = new Tag("friends");
        Tag secondTag = new Tag("retrieveTester");
        for (ReadOnlyPerson person : model.getFilteredPersonList()) {
            Person untaggedPerson = new PersonBuilder(person).build();
            UniqueTagList updatedTags = new UniqueTagList(untaggedPerson.getTags());
            updatedTags.remove(firstTag);
            updatedTags.remove(secondTag);
            untaggedPerson.setTags(updatedTags.toSet());
            expectedModel.updatePerson(person, untaggedPerson);
        }

        expectedModel.deleteUnusedTag(firstTag);
        expectedModel.deleteUnusedTag(secondTag);

        UntagCommand command = prepareCommand(true,
                Collections.emptyList(), Arrays.asList(firstTag, secondTag));

        String expectedMessage = String.format(MESSAGE_SUCCESS_MULTIPLE_TAGS_IN_LIST,
                firstTag.toString() + ", " + secondTag.toString());

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_allTagsInFilteredList_success() throws Exception {
        showFirstAndSecondPersonsOnly(model);

        ReadOnlyPerson firstPersonInList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person firstUntaggedPerson = new PersonBuilder(firstPersonInList).withTags().build();
        ReadOnlyPerson secondPersonInList = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person secondUntaggedPerson = new PersonBuilder(secondPersonInList).withTags().build();
        UntagCommand command = prepareCommand(true, Collections.emptyList(), Collections.emptyList());

        String expectedMessage = MESSAGE_SUCCESS_ALL_TAGS_IN_LIST;

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());
        showFirstAndSecondPersonsOnly(expectedModel);

        expectedModel.updatePerson(model.getFilteredPersonList().get(0), firstUntaggedPerson);
        expectedModel.updatePerson(model.getFilteredPersonList().get(1), secondUntaggedPerson);
        expectedModel.deleteUnusedTag(new Tag("friends"));
        expectedModel.deleteUnusedTag(new Tag("owesMoney"));
        expectedModel.deleteUnusedTag(new Tag("retrieveTester"));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_severalTagsInFilteredList_success() throws Exception {
        showFirstAndSecondPersonsOnly(model);

        ReadOnlyPerson firstPersonInList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person firstUntaggedPerson = new PersonBuilder(firstPersonInList).withTags().build();
        ReadOnlyPerson secondPersonInList = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person secondUntaggedPerson = new PersonBuilder(secondPersonInList).withTags("owesMoney").build();
        Tag firstTag = new Tag("friends");
        Tag secondTag = new Tag("retrieveTester");
        UntagCommand command = prepareCommand(true, Collections.emptyList(),
                Arrays.asList(firstTag, secondTag));

        String expectedMessage = String.format(MESSAGE_SUCCESS_MULTIPLE_TAGS_IN_LIST,
                firstTag.toString() + ", " + secondTag.toString());

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());
        showFirstAndSecondPersonsOnly(expectedModel);

        expectedModel.updatePerson(model.getFilteredPersonList().get(0), firstUntaggedPerson);
        expectedModel.updatePerson(model.getFilteredPersonList().get(1), secondUntaggedPerson);
        expectedModel.deleteUnusedTag(new Tag("friends"));
        expectedModel.deleteUnusedTag(new Tag("retrieveTester"));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_allTagsOfSelectedPersonsInList_success() throws Exception {
        ReadOnlyPerson firstPersonInList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person firstUntaggedPerson = new PersonBuilder(firstPersonInList).withTags().build();
        ReadOnlyPerson secondPersonInList = model.getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        Person secondUntaggedPerson = new PersonBuilder(secondPersonInList).withTags().build();
        UntagCommand command = prepareCommand(false,
                Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON), Collections.emptyList());

        String expectedMessage = String.format(MESSAGE_SUCCESS_ALL_TAGS, 2) + " "
                + firstUntaggedPerson.getName().toString() + ", " + secondUntaggedPerson.getName().toString();

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()),
                model.getDatabase(), new UserPrefs());

        expectedModel.updatePerson(model.getFilteredPersonList().get(0), firstUntaggedPerson);
        expectedModel.updatePerson(model.getFilteredPersonList().get(1), secondUntaggedPerson);
        expectedModel.deleteUnusedTag(new Tag("friends"));
        expectedModel.deleteUnusedTag(new Tag("owesMoney"));
        expectedModel.deleteUnusedTag(new Tag("retrieveTester"));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidPersonIndexesUnfilteredList_failure() throws Exception {
        Index outOfBound = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        UntagCommand command = prepareCommand(false, Arrays.asList(INDEX_FIRST_PERSON, outOfBound),
                Arrays.asList(new Tag("tagOne"), new Tag("tagTwo")));

        assertCommandFailure(command, model, MESSAGE_INVALID_INDEXES);
    }

    @Test
    public void execute_invalidPersonIndexesFilteredList_failure() throws Exception {
        showFirstAndSecondPersonsOnly(model);

        Index outOfBoundIndex = INDEX_THIRD_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        UntagCommand command = prepareCommand(false, Arrays.asList(INDEX_FIRST_PERSON, outOfBoundIndex),
                Arrays.asList(new Tag("friends"), new Tag("randomTag")));

        assertCommandFailure(command, model, MESSAGE_INVALID_INDEXES);
    }

    @Test
    public void execute_tagsNotFound_failure() throws Exception {
        Tag firstNotFoundTag = new Tag("tagOne");
        Tag secondNotFoundTag = new Tag("tagTwo");

        Set<Tag> uniqueTags = new HashSet<>();
        for (ReadOnlyPerson person : model.getAddressBook().getPersonList()) {
            uniqueTags.addAll(person.getTags());
        }
        StringJoiner joiner = new StringJoiner(", ");
        for (Tag tag : uniqueTags) {
            joiner.add(tag.toString());
        }

        UntagCommand command = prepareCommand(false,
                Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON),
                Arrays.asList(firstNotFoundTag, secondNotFoundTag));

        String expectedMessage = String.format(MESSAGE_TAG_NOT_FOUND,
                firstNotFoundTag.toString() + ", " + secondNotFoundTag.toString(), joiner.toString());

        assertCommandFailure(command, model, expectedMessage);
    }

    @Test
    public void equals() throws Exception {
        final List<Index> indexList = Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        final List<Tag> tagList = Arrays.asList(new Tag("friends"), new Tag("enemies"));
        final UntagCommand firstCommandCase = new UntagCommand(false,
                indexList, tagList);
        final UntagCommand secondCommandCase = new UntagCommand(false,
                indexList, Collections.emptyList());
        final UntagCommand thirdCommandCase = new UntagCommand(true,
                Collections.emptyList(), tagList);
        final UntagCommand fourthCommandCase = new UntagCommand(true,
                Collections.emptyList(), Collections.emptyList());

        // same values -> returns true
        final List<Index> indexListCopy = Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON);
        final List<Tag> tagListCopy = Arrays.asList(new Tag("friends"), new Tag("enemies"));
        assertTrue(firstCommandCase.equals(new UntagCommand(false,
                indexListCopy, tagListCopy)));
        assertTrue(secondCommandCase.equals(new UntagCommand(false,
                indexListCopy, Collections.emptyList())));
        assertTrue(thirdCommandCase.equals(new UntagCommand(true,
                Collections.emptyList(), tagListCopy)));
        assertTrue(fourthCommandCase.equals(new UntagCommand(true,
                Collections.emptyList(), Collections.emptyList())));

        // same object -> returns true
        assertTrue(firstCommandCase.equals(firstCommandCase));
        assertTrue(secondCommandCase.equals(secondCommandCase));
        assertTrue(thirdCommandCase.equals(thirdCommandCase));
        assertTrue(fourthCommandCase.equals(fourthCommandCase));

        // null -> returns false
        assertFalse(firstCommandCase.equals(null));
        assertFalse(secondCommandCase.equals(null));
        assertFalse(thirdCommandCase.equals(null));
        assertFalse(fourthCommandCase.equals(null));

        // different types -> returns false
        assertFalse(firstCommandCase.equals(new ClearCommand()));
        assertFalse(secondCommandCase.equals(new ClearCommand()));
        assertFalse(thirdCommandCase.equals(new ClearCommand()));
        assertFalse(fourthCommandCase.equals(new ClearCommand()));

        // different index list -> returns false
        final List<Index> anotherIndexList = Arrays.asList(INDEX_FIRST_PERSON, INDEX_THIRD_PERSON);
        assertFalse(firstCommandCase.equals(new UntagCommand(false,
                anotherIndexList, tagList)));
        assertFalse(secondCommandCase.equals(new UntagCommand(false,
                anotherIndexList, Collections.emptyList())));

        // different tag -> returns false
        final List<Tag> anotherTagList = Arrays.asList(new Tag("friends"), new Tag("owesMoney"));
        assertFalse(firstCommandCase.equals(new UntagCommand(false,
                indexList, anotherTagList)));
        assertFalse(thirdCommandCase.equals(new UntagCommand(false,
                Collections.emptyList(), anotherTagList)));
    }

    /**
     * Returns an {@code UntagCommand}.
     */
    private UntagCommand prepareCommand(Boolean toAllPersonsInFilteredList, List<Index> indexes, List<Tag> tags) {
        UntagCommand command = new UntagCommand(toAllPersonsInFilteredList, indexes, tags);
        command.setData(model, new CommandHistory(), new UndoRedoStack());
        return command;
    }

}
```
###### \java\seedu\address\logic\commands\ViewCommandTest.java
``` java

package seedu.address.logic.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static seedu.address.logic.commands.CommandTestUtil.showFirstPersonOnly;
import static seedu.address.testutil.TypicalAccounts.getTypicalDatabase;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.events.ui.ShowProfileRequestEvent;
import seedu.address.logic.CommandHistory;
import seedu.address.logic.UndoRedoStack;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.ui.testutil.EventsCollectorRule;

/**
 * Contains integration tests (interaction with the Model) for {@code ViewCommand}.
 */
public class ViewCommandTest {
    @Rule
    public final EventsCollectorRule eventsCollectorRule = new EventsCollectorRule();

    private Model model;

    @Before
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), getTypicalDatabase(), new UserPrefs());
    }

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Index lastPersonIndex = Index.fromOneBased(model.getFilteredPersonList().size());

        assertExecutionSuccess(INDEX_FIRST_PERSON);
        assertExecutionSuccess(INDEX_THIRD_PERSON);
        assertExecutionSuccess(lastPersonIndex);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showFirstPersonOnly(model);

        assertExecutionSuccess(INDEX_FIRST_PERSON);
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showFirstPersonOnly(model);

        Index outOfBoundsIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundsIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        assertExecutionFailure(outOfBoundsIndex, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ViewCommand command = new ViewCommand(INDEX_FIRST_PERSON);

        // same object -> returns true
        assertTrue(command.equals(command));

        // same values -> returns true
        ViewCommand commandCopy = new ViewCommand(INDEX_FIRST_PERSON);
        assertTrue(command.equals(commandCopy));

        // different types -> returns false
        assertFalse(command.equals(new ClearCommand()));

        // null -> returns false
        assertFalse(command.equals(null));

        // different person -> returns false
        ViewCommand anotherCommand = new ViewCommand(INDEX_SECOND_PERSON);
        assertFalse(command.equals(anotherCommand));
    }

    /**
     * Executes a {@code ViewCommand} with the given {@code index}, and checks that {@code ShowProfileRequestEvent}
     * is raised with the correct person.
     */
    private void assertExecutionSuccess(Index index) {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();
        ReadOnlyPerson personToViewProfile = lastShownList.get(index.getZeroBased());
        ViewCommand command = prepareCommand(index);

        try {
            CommandResult commandResult = command.execute();
            assertEquals(String.format(ViewCommand.MESSAGE_VIEW_PROFILE_SUCCESS,
                    personToViewProfile.getName().toString()), commandResult.feedbackToUser);
        } catch (CommandException ce) {
            throw new IllegalArgumentException("Execution of command should not fail.", ce);
        }

        ShowProfileRequestEvent lastEvent =
                (ShowProfileRequestEvent) eventsCollectorRule.eventsCollector.getMostRecent();
        assertEquals(personToViewProfile, (ReadOnlyPerson) new Person(lastEvent.person));
    }

    /**
     * Executes a {@code ViewCommand} with the given {@code index}, and checks that a {@code CommandException}
     * is thrown with the {@code expectedMessage}.
     */
    private void assertExecutionFailure(Index index, String expectedMessage) {
        ViewCommand viewCommand = prepareCommand(index);

        try {
            viewCommand.execute();
            fail("The expected CommandException was not thrown.");
        } catch (CommandException ce) {
            assertEquals(expectedMessage, ce.getMessage());
            assertTrue(eventsCollectorRule.eventsCollector.isEmpty());
        }
    }

    /**
     * Returns a {@code ViewCommand} with parameters {@code index}.
     */
    private ViewCommand prepareCommand(Index index) {
        ViewCommand viewCommand = new ViewCommand(index);
        viewCommand.setData(model, new CommandHistory(), new UndoRedoStack());
        return viewCommand;
    }
}
```
###### \java\seedu\address\logic\parser\AddReminderCommandParserTest.java
``` java

package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.DATE_DESC_ASSIGNMENT;
import static seedu.address.logic.commands.CommandTestUtil.DATE_DESC_PROJECT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_DATE_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_PRIORITY_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_DESC;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TASK_DESC;
import static seedu.address.logic.commands.CommandTestUtil.MESSAGE_DESC_ASSIGNMENT;
import static seedu.address.logic.commands.CommandTestUtil.MESSAGE_DESC_PROJECT;
import static seedu.address.logic.commands.CommandTestUtil.PRIORITY_DESC_ASSIGNMENT;
import static seedu.address.logic.commands.CommandTestUtil.PRIORITY_DESC_PROJECT;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_OFFICE;
import static seedu.address.logic.commands.CommandTestUtil.TAG_DESC_SOFTCOPY;
import static seedu.address.logic.commands.CommandTestUtil.TASK_DESC_ASSIGNMENT;
import static seedu.address.logic.commands.CommandTestUtil.TASK_DESC_PROJECT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DATE_ASSIGNMENT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_DATE_PROJECT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MESSAGE_ASSIGNMENT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_MESSAGE_PROJECT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PRIORITY_ASSIGNMENT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PRIORITY_PROJECT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_OFFICE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_SOFTCOPY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TASK_ASSIGNMENT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TASK_PROJECT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import seedu.address.logic.commands.AddReminderCommand;
import seedu.address.model.reminder.Date;
import seedu.address.model.reminder.Priority;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.reminder.Task;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.ReminderBuilder;

public class AddReminderCommandParserTest {
    private AddReminderCommandParser parser = new AddReminderCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        Reminder expectedReminder = new ReminderBuilder().withTask(VALID_TASK_ASSIGNMENT)
                .withPriority(VALID_PRIORITY_ASSIGNMENT).withDate(VALID_DATE_ASSIGNMENT)
                .withMessage(VALID_MESSAGE_ASSIGNMENT).withTags(VALID_TAG_SOFTCOPY).build();

        // multiple task names - last task name accepted
        assertParseSuccess(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_PROJECT + TASK_DESC_ASSIGNMENT
                + PRIORITY_DESC_ASSIGNMENT + DATE_DESC_ASSIGNMENT + MESSAGE_DESC_ASSIGNMENT
                + TAG_DESC_SOFTCOPY, new AddReminderCommand(expectedReminder));

        // multiple priorities - last priority accepted
        assertParseSuccess(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_ASSIGNMENT
                + PRIORITY_DESC_PROJECT + PRIORITY_DESC_ASSIGNMENT + DATE_DESC_ASSIGNMENT + MESSAGE_DESC_ASSIGNMENT
                + TAG_DESC_SOFTCOPY, new AddReminderCommand(expectedReminder));

        // multiple dates - last date accepted
        assertParseSuccess(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_ASSIGNMENT
                + PRIORITY_DESC_ASSIGNMENT + DATE_DESC_PROJECT + DATE_DESC_ASSIGNMENT + MESSAGE_DESC_ASSIGNMENT
                + TAG_DESC_SOFTCOPY, new AddReminderCommand(expectedReminder));

        // multiple messages - last message accepted
        assertParseSuccess(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_ASSIGNMENT
                + PRIORITY_DESC_ASSIGNMENT + DATE_DESC_ASSIGNMENT + MESSAGE_DESC_PROJECT + MESSAGE_DESC_ASSIGNMENT
                + TAG_DESC_SOFTCOPY, new AddReminderCommand(expectedReminder));

        // multiple tags - all accepted
        Reminder expectedReminderMultipleTags = new ReminderBuilder().withTask(VALID_TASK_ASSIGNMENT)
                .withPriority(VALID_PRIORITY_ASSIGNMENT).withDate(VALID_DATE_ASSIGNMENT)
                .withMessage(VALID_MESSAGE_ASSIGNMENT).withTags(VALID_TAG_OFFICE, VALID_TAG_SOFTCOPY).build();
        assertParseSuccess(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_ASSIGNMENT
                + PRIORITY_DESC_ASSIGNMENT + DATE_DESC_ASSIGNMENT + MESSAGE_DESC_ASSIGNMENT + TAG_DESC_SOFTCOPY
                + TAG_DESC_OFFICE, new AddReminderCommand(expectedReminderMultipleTags));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        // zero tags
        Reminder expectedReminder = new ReminderBuilder().withTask(VALID_TASK_PROJECT)
                .withPriority(VALID_PRIORITY_PROJECT).withDate(VALID_DATE_PROJECT).withMessage(VALID_MESSAGE_PROJECT)
                .withTags().build();
        assertParseSuccess(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_PROJECT + PRIORITY_DESC_PROJECT
                + DATE_DESC_PROJECT + MESSAGE_DESC_PROJECT, new AddReminderCommand(expectedReminder));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddReminderCommand.MESSAGE_USAGE);

        // missing task name prefix
        assertParseFailure(parser, AddReminderCommand.COMMAND_WORD + VALID_TASK_ASSIGNMENT
                + PRIORITY_DESC_ASSIGNMENT + DATE_DESC_ASSIGNMENT + MESSAGE_DESC_ASSIGNMENT, expectedMessage);

        // missing priority prefix
        assertParseFailure(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_ASSIGNMENT
                + VALID_PRIORITY_ASSIGNMENT + DATE_DESC_ASSIGNMENT + MESSAGE_DESC_ASSIGNMENT, expectedMessage);

        // missing date prefix
        assertParseFailure(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_ASSIGNMENT
                + PRIORITY_DESC_ASSIGNMENT + VALID_DATE_ASSIGNMENT + MESSAGE_DESC_ASSIGNMENT, expectedMessage);

        // missing message prefix
        assertParseFailure(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_ASSIGNMENT
                + PRIORITY_DESC_ASSIGNMENT + DATE_DESC_ASSIGNMENT + VALID_MESSAGE_ASSIGNMENT, expectedMessage);

        // all prefixes missing
        assertParseFailure(parser, AddReminderCommand.COMMAND_WORD + VALID_TASK_ASSIGNMENT
                + VALID_PRIORITY_ASSIGNMENT + VALID_DATE_ASSIGNMENT + VALID_MESSAGE_ASSIGNMENT, expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // invalid task name
        assertParseFailure(parser, AddReminderCommand.COMMAND_WORD + INVALID_TASK_DESC
                + PRIORITY_DESC_ASSIGNMENT + DATE_DESC_ASSIGNMENT + MESSAGE_DESC_ASSIGNMENT + TAG_DESC_OFFICE
                + TAG_DESC_SOFTCOPY, Task.MESSAGE_TASK_NAME_CONSTRAINTS);

        // invalid priority
        assertParseFailure(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_ASSIGNMENT
                + INVALID_PRIORITY_DESC + DATE_DESC_ASSIGNMENT + MESSAGE_DESC_ASSIGNMENT + TAG_DESC_OFFICE
                + TAG_DESC_SOFTCOPY, Priority.MESSAGE_PRIORITY_CONSTRAINTS);

        // invalid date
        assertParseFailure(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_ASSIGNMENT
                + PRIORITY_DESC_ASSIGNMENT + INVALID_DATE_DESC + MESSAGE_DESC_ASSIGNMENT + TAG_DESC_OFFICE
                + TAG_DESC_SOFTCOPY, Date.MESSAGE_DATE_CONSTRAINTS);

        // invalid tag
        assertParseFailure(parser, AddReminderCommand.COMMAND_WORD + TASK_DESC_ASSIGNMENT
                + PRIORITY_DESC_ASSIGNMENT + DATE_DESC_ASSIGNMENT + MESSAGE_DESC_ASSIGNMENT + INVALID_TAG_DESC
                + VALID_TAG_SOFTCOPY, Tag.MESSAGE_TAG_CONSTRAINTS);

        // two invalid values, only first invalid value reported
        assertParseFailure(parser, AddReminderCommand.COMMAND_WORD + INVALID_TASK_DESC + INVALID_PRIORITY_DESC
                + DATE_DESC_ASSIGNMENT + MESSAGE_DESC_ASSIGNMENT, Task.MESSAGE_TASK_NAME_CONSTRAINTS);
    }
}
```
###### \java\seedu\address\logic\parser\AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_retrieve() throws Exception {
        RetrieveCommand command = (RetrieveCommand) parser.parseCommand(RetrieveCommand.COMMAND_WORD + " " + "friends");
        assertEquals(new RetrieveCommand(new TagContainsKeywordPredicate(new Tag("friends"))), command);
    }
```
###### \java\seedu\address\logic\parser\AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_view() throws Exception {
        ViewCommand command = (ViewCommand) parser.parseCommand(
                ViewCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
        assertEquals(new ViewCommand(INDEX_FIRST_PERSON), command);
    }
```
###### \java\seedu\address\logic\parser\AddressBookParserTest.java
``` java
    @Test
    public void parseCommand_tag() throws Exception {
        TagCommand command = (TagCommand) parser.parseCommand(TagCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + ","
                + INDEX_SECOND_PERSON.getOneBased() + ","
                + INDEX_THIRD_PERSON.getOneBased() + " " + "friends");
        assertEquals(new TagCommand(Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON, INDEX_THIRD_PERSON),
                new Tag("friends")), command);
    }

    @Test
    public void parseCommand_untag() throws Exception {
        UntagCommand command = (UntagCommand) parser.parseCommand(UntagCommand.COMMAND_WORD + " "
                + INDEX_FIRST_PERSON.getOneBased() + ","
                + INDEX_SECOND_PERSON.getOneBased() + ","
                + INDEX_THIRD_PERSON.getOneBased() + " " + "friends/enemies");
        Tag firstTag = new Tag("friends");
        Tag secondTag = new Tag("enemies");
        assertEquals(new UntagCommand(false, Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                INDEX_THIRD_PERSON), Arrays.asList(secondTag, firstTag)), command);

        command = (UntagCommand) parser.parseCommand(UntagCommand.COMMAND_WORD + " 1,2,3");
        assertEquals(new UntagCommand(false, Arrays.asList(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                INDEX_THIRD_PERSON), Collections.emptyList()), command);

        command = (UntagCommand) parser.parseCommand(UntagCommand.COMMAND_WORD + " -all " + "friends/enemies");
        assertEquals(new UntagCommand(true, Collections.emptyList(),
                Arrays.asList(secondTag, firstTag)), command);

        command = (UntagCommand) parser.parseCommand(UntagCommand.COMMAND_WORD + " -all");
        assertEquals(new UntagCommand(true, Collections.emptyList(), Collections.emptyList()), command);
    }

    @Test
    public void parseCommand_retag() throws Exception {
        RetagCommand command = (RetagCommand) parser.parseCommand(RetagCommand.COMMAND_WORD + " "
                + "enemies" + " " + "friends");
        assertEquals(new RetagCommand(new Tag("enemies"), new Tag("friends")), command);
    }
```
###### \java\seedu\address\logic\parser\DeleteReminderCommandParserTest.java
``` java

package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_REMINDER;

import org.junit.Test;

import seedu.address.logic.commands.DeleteReminderCommand;

public class DeleteReminderCommandParserTest {
    private DeleteReminderCommandParser parser = new DeleteReminderCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteReminderCommand() {
        assertParseSuccess(parser, "1", new DeleteReminderCommand(INDEX_FIRST_REMINDER));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                DeleteReminderCommand.MESSAGE_USAGE));
    }
}
```
###### \java\seedu\address\logic\parser\RetagCommandParserTest.java
``` java

package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.RetagCommand.MESSAGE_INVALID_ARGS;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.tag.Tag.MESSAGE_TAG_CONSTRAINTS;

import org.junit.Test;

import seedu.address.logic.commands.RetagCommand;
import seedu.address.model.tag.Tag;

public class RetagCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, RetagCommand.MESSAGE_USAGE);

    private static final String MESSAGE_DUPLICATED_TAG_NAMES =
            String.format(MESSAGE_INVALID_ARGS, RetagCommand.MESSAGE_USAGE);

    private RetagCommandParser parser = new RetagCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no tag name specified
        assertParseFailure(parser, "     ", MESSAGE_INVALID_FORMAT);

        // only one tag name specified
        assertParseFailure(parser, " friends  ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidArg_throwsParseException() {
        // invalid tag name
        assertParseFailure(parser, "friends !@#$!", MESSAGE_TAG_CONSTRAINTS);

        // target tag name is the same as new tag name
        assertParseFailure(parser, "friends friends", MESSAGE_DUPLICATED_TAG_NAMES);
    }

    @Test
    public void parse_validArgs_returnsRetagCommand() throws Exception {
        Tag newTag = new Tag("friends");
        Tag targetTag = new Tag("enemies");

        // no leading and trailing whitespaces
        RetagCommand expectedCommand = new RetagCommand(targetTag, newTag);
        assertParseSuccess(parser, "enemies friends", expectedCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, "\t enemies \n friends \t \n", expectedCommand);
    }
}
```
###### \java\seedu\address\logic\parser\RetrieveCommandParserTest.java
``` java

package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.tag.Tag.MESSAGE_TAG_CONSTRAINTS;

import org.junit.Test;

import seedu.address.logic.commands.RetrieveCommand;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.TagContainsKeywordPredicate;

public class RetrieveCommandParserTest {

    private RetrieveCommandParser parser = new RetrieveCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        final String expectedMessage = String.format(RetrieveCommand.MESSAGE_EMPTY_ARGS, RetrieveCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "     ", expectedMessage);
    }

    @Test
    public void parse_invalidArg_throwsParseException() {
        assertParseFailure(parser, "*&%nonAlphanumericCharacters!!!%&*", MESSAGE_TAG_CONSTRAINTS);
    }

    @Test
    public void parse_validArgs_returnsRetrieveCommand() throws Exception {
        TagContainsKeywordPredicate predicate = new TagContainsKeywordPredicate(new Tag("friends"));

        // no leading and trailing whitespaces
        RetrieveCommand expectedCommand =
                new RetrieveCommand(predicate);
        assertParseSuccess(parser, "friends", expectedCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, "\n friends \t \n", expectedCommand);
    }

}
```
###### \java\seedu\address\logic\parser\TagCommandParserTest.java
``` java

package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.TagCommand.MESSAGE_EMPTY_INDEX_LIST;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.tag.Tag.MESSAGE_TAG_CONSTRAINTS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import java.util.Arrays;

import org.junit.Test;

import seedu.address.logic.commands.TagCommand;
import seedu.address.model.tag.Tag;

public class TagCommandParserTest {

    private static final String VALID_TAG_NAME = "friends";

    private static final String VALID_INDEX_LIST = "1,2,3";

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE);

    private static final String MESSAGE_NO_INDEXES =
            String.format(MESSAGE_EMPTY_INDEX_LIST, TagCommand.MESSAGE_USAGE);

    private TagCommandParser parser = new TagCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no indexes specified
        assertParseFailure(parser, VALID_TAG_NAME, MESSAGE_INVALID_FORMAT);

        // no tag name specified
        assertParseFailure(parser, VALID_INDEX_LIST, MESSAGE_INVALID_FORMAT);

        // no indexes and no tag name specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // no indexes
        assertParseFailure(parser, ",,,, " + VALID_TAG_NAME, MESSAGE_NO_INDEXES);

        // negative index
        assertParseFailure(parser, "-5 " + VALID_TAG_NAME, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0 " + VALID_TAG_NAME, MESSAGE_INVALID_FORMAT);

        // indexes are not all integers
        assertParseFailure(parser, "1,2,three " + VALID_TAG_NAME, MESSAGE_INVALID_FORMAT);

        // invalid tag name
        assertParseFailure(parser, VALID_INDEX_LIST + " !@#$", MESSAGE_TAG_CONSTRAINTS);

        // invalid arguments being parsed
        assertParseFailure(parser, "1,2,three dummy tag", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_validArgs_returnsTagCommand() throws Exception {
        // no leading and trailing whitespaces
        TagCommand expectedCommand = new TagCommand(Arrays.asList(INDEX_FIRST_PERSON,
                INDEX_SECOND_PERSON, INDEX_THIRD_PERSON), new Tag(VALID_TAG_NAME));
        assertParseSuccess(parser, VALID_INDEX_LIST + " " + VALID_TAG_NAME, expectedCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, "\t " + VALID_INDEX_LIST + " \n"
                + VALID_TAG_NAME + "\t \n", expectedCommand);

        // multiple duplicated indexes
        assertParseSuccess(parser, "1,1,1,2,2,3" + " " + VALID_TAG_NAME, expectedCommand);
    }

}
```
###### \java\seedu\address\logic\parser\UntagCommandParserTest.java
``` java

package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.UntagCommand.MESSAGE_EMPTY_INDEX_LIST;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.model.tag.Tag.MESSAGE_TAG_CONSTRAINTS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import seedu.address.logic.commands.UntagCommand;
import seedu.address.model.tag.Tag;

public class UntagCommandParserTest {

    private static final String VALID_TAG_NAMES = "friends/enemies";

    private static final String VALID_INDEX_LIST = "1,2,3";

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, UntagCommand.MESSAGE_USAGE);

    private static final String MESSAGE_NO_INDEXES =
            String.format(MESSAGE_EMPTY_INDEX_LIST, UntagCommand.MESSAGE_USAGE);

    private UntagCommandParser parser = new UntagCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no indexes specified
        assertParseFailure(parser, VALID_TAG_NAMES, MESSAGE_INVALID_FORMAT);

        // no indexes and no tag name specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        // no indexes
        assertParseFailure(parser, ",,,, " + VALID_TAG_NAMES, MESSAGE_NO_INDEXES);

        // negative index
        assertParseFailure(parser, "-5,-1" + VALID_TAG_NAMES, MESSAGE_INVALID_FORMAT);

        // zero index
        assertParseFailure(parser, "0, 0,  0 " + VALID_TAG_NAMES, MESSAGE_INVALID_FORMAT);

        // indexes are not all integers
        assertParseFailure(parser, "1,2,three " + VALID_TAG_NAMES, MESSAGE_INVALID_FORMAT);

        // invalid tag name
        assertParseFailure(parser, VALID_INDEX_LIST + " friends/!@#$", MESSAGE_TAG_CONSTRAINTS);

        // invalid arguments being parsed
        assertParseFailure(parser, "1,2,three dummy friends/enemies", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_validArgs_returnsUntagCommand() throws Exception {
        Tag firstTag = new Tag("friends");
        Tag secondTag = new Tag("enemies");

        // no leading and trailing whitespaces
        UntagCommand expectedCommand = new UntagCommand(false, Arrays.asList(INDEX_FIRST_PERSON,
                INDEX_SECOND_PERSON, INDEX_THIRD_PERSON), Arrays.asList(secondTag, firstTag));
        assertParseSuccess(parser, VALID_INDEX_LIST + " " + VALID_TAG_NAMES, expectedCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, "\t " + VALID_INDEX_LIST + " \n"
                + VALID_TAG_NAMES + "\t \n", expectedCommand);

        // multiple duplicated indexes
        assertParseSuccess(parser, "1,1,1,2,2,3" + " " + VALID_TAG_NAMES, expectedCommand);

        // remove all tags from the specified persons
        expectedCommand = new UntagCommand(false, Arrays.asList(INDEX_FIRST_PERSON,
                INDEX_SECOND_PERSON, INDEX_THIRD_PERSON), Collections.emptyList());
        assertParseSuccess(parser, "  1,2,3  ", expectedCommand);

        // remove all tags
        expectedCommand = new UntagCommand(true, Collections.emptyList(), Collections.emptyList());
        assertParseSuccess(parser, "  -all  ", expectedCommand);

        // remove a tag from all persons
        expectedCommand = new UntagCommand(true, Collections.emptyList(),
                Arrays.asList(secondTag, firstTag));
        assertParseSuccess(parser, " -all  " + " friends/enemies  ", expectedCommand);
    }

}
```
###### \java\seedu\address\logic\parser\ViewCommandParserTest.java
``` java

package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.Test;

import seedu.address.logic.commands.ViewCommand;

/**
 * Test scope: similar to {@code DeleteCommandParserTest}.
 * @see DeleteCommandParserTest
 */
public class ViewCommandParserTest {

    private ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_validArgs_returnsViewCommand() {
        assertParseSuccess(parser, "1", new ViewCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, "a", String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
    }
}
```
###### \java\seedu\address\model\reminder\DateTest.java
``` java

package seedu.address.model.reminder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DateTest {

    @Test
    public void isValidDate() {
        // invalid date
        assertFalse(Date.isValidDate("")); // empty string
        assertFalse(Date.isValidDate(" ")); // spaces only
        assertFalse(Date.isValidDate("0abc/01/1998 12:30")); // contains non-numeric characters
        assertFalse(Date.isValidDate("1998/01/01 12:30")); // wrong format
        assertFalse(Date.isValidDate("1/1/2017 6:30")); // without zeroes
        assertFalse(Date.isValidDate("32/01/2017 12:30")); // invalid day
        assertFalse(Date.isValidDate("01/13/2017 12:30")); // invalid month
        assertFalse(Date.isValidDate("01/01/20170 12:30")); // invalid year
        assertFalse(Date.isValidDate("01/01/2017 25:30")); // invalid hour
        assertFalse(Date.isValidDate("01/01/2017 12:60")); // invalid minute

        // valid date
        assertTrue(Date.isValidDate("10/08/2017 20:30"));
        assertTrue(Date.isValidDate("    01/01/2017    12:30  ")); // trailing space
    }
}
```
###### \java\seedu\address\model\reminder\MessageTest.java
``` java

package seedu.address.model.reminder;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MessageTest {

    @Test
    public void isValidMessage() {
        assertTrue(Message.isValidMessage("Buy present for friend."));
    }
}
```
###### \java\seedu\address\model\reminder\PriorityTest.java
``` java

package seedu.address.model.reminder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PriorityTest {

    @Test
    public void isValidPriority() {
        // invalid priority
        assertFalse(Priority.isValidPriority("")); // empty string
        assertFalse(Priority.isValidPriority(" ")); // spaces only
        assertFalse(Priority.isValidPriority("12345")); // less than 3 numbers
        assertFalse(Priority.isValidPriority("priority")); // non-numeric
        assertFalse(Priority.isValidPriority("Pr90i0ori23ty")); // alphabets within digits
        assertFalse(Priority.isValidPriority("1234 5678")); // spaces within digits
        assertFalse(Priority.isValidPriority("LOW")); // capital letters
        assertFalse(Priority.isValidPriority("LoW")); // mixed non-capital and capital letters
        assertFalse(Priority.isValidPriority("Low Medium High")); // multiple priorities

        // valid priority
        assertTrue(Priority.isValidPriority("Low"));
        assertTrue(Priority.isValidPriority("Medium"));
        assertTrue(Priority.isValidPriority("High"));
    }
}
```
###### \java\seedu\address\model\reminder\TaskTest.java
``` java

package seedu.address.model.reminder;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TaskTest {

    @Test
    public void isValidTaskName() {
        // invalid task name
        assertFalse(Task.isValidTaskName("")); // empty string
        assertFalse(Task.isValidTaskName(" ")); // spaces only
        assertFalse(Task.isValidTaskName("^")); // only non-alphanumeric characters
        assertFalse(Task.isValidTaskName("birthday*")); // contains non-alphanumeric characters

        // valid task name
        assertTrue(Task.isValidTaskName("birthday")); // alphabets only
        assertTrue(Task.isValidTaskName("12345")); // numbers only
        assertTrue(Task.isValidTaskName("birthday 2morrow")); // alphanumeric characters
        assertTrue(Task.isValidTaskName("Birthday tomorrow")); // with capital letters
        assertTrue(Task.isValidTaskName("Tomorrow is my birthday")); // long task names
    }
}
```
###### \java\seedu\address\model\tag\TagContainsKeywordPredicateTest.java
``` java

package seedu.address.model.tag;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import seedu.address.testutil.PersonBuilder;

public class TagContainsKeywordPredicateTest {

    @Test
    public void equals() throws Exception {
        TagContainsKeywordPredicate predicate = new TagContainsKeywordPredicate(new Tag("friends"));

        // same values -> returns true
        assertTrue(predicate.equals(new TagContainsKeywordPredicate(new Tag("friends"))));

        // same object -> returns true
        assertTrue(predicate.equals(predicate));

        // different types -> returns false
        assertFalse(predicate.equals(1));

        // null -> returns false
        assertFalse(predicate.equals(null));

        // different person -> returns false
        assertFalse(predicate.equals(new TagContainsKeywordPredicate(new Tag("family"))));
    }

    @Test
    public void test_tagFound_returnsTrue() throws Exception {
        TagContainsKeywordPredicate predicate = new TagContainsKeywordPredicate(new Tag("friends"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").withTags("friends", "tester").build()));
    }

    @Test
    public void test_tagNotFound_returnsFalse() throws Exception {
        TagContainsKeywordPredicate predicate = new TagContainsKeywordPredicate(new Tag("friends"));
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withTags("family", "tester").build()));
    }

}
```
###### \java\seedu\address\ui\ReminderCardTest.java
``` java

package seedu.address.ui;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static seedu.address.testutil.TypicalReminders.BIRTHDAY;
import static seedu.address.ui.testutil.GuiTestAssert.assertCardDisplaysReminder;

import org.junit.Test;

import guitests.guihandles.ReminderCardHandle;
import seedu.address.model.reminder.ReadOnlyReminder;
import seedu.address.model.reminder.Reminder;
import seedu.address.testutil.ReminderBuilder;

public class ReminderCardTest extends GuiUnitTest {

    @Test
    public void display() {
        // no tags
        Reminder reminderWithNoTags = new ReminderBuilder().withTags(new String[0]).build();
        ReminderCard reminderCard = new ReminderCard(reminderWithNoTags, 1);
        uiPartRule.setUiPart(reminderCard);
        assertCardDisplay(reminderCard, reminderWithNoTags, 1);

        // with tags
        Reminder reminderWithTags = new ReminderBuilder().build();
        reminderCard = new ReminderCard(reminderWithTags, 2);
        uiPartRule.setUiPart(reminderCard);
        assertCardDisplay(reminderCard, reminderWithTags, 2);

        // changes made to Reminder reflects on card
        guiRobot.interact(() -> {
            reminderWithTags.setTask(BIRTHDAY.getTask());
            reminderWithTags.setPriority(BIRTHDAY.getPriority());
            reminderWithTags.setDate(BIRTHDAY.getDate());
            reminderWithTags.setMessage(BIRTHDAY.getMessage());
            reminderWithTags.setTags(BIRTHDAY.getTags());
        });
        assertCardDisplay(reminderCard, reminderWithTags, 2);
    }

    @Test
    public void equals() {
        Reminder reminder = new ReminderBuilder().build();
        ReminderCard reminderCard = new ReminderCard(reminder, 0);

        // same reminder, same index -> returns true
        ReminderCard copy = new ReminderCard(reminder, 0);
        assertTrue(reminderCard.equals(copy));

        // same object -> returns true
        assertTrue(reminderCard.equals(reminderCard));

        // null -> returns false
        assertFalse(reminderCard.equals(null));

        // different types -> returns false
        assertFalse(reminderCard.equals(0));

        // different reminder, same index -> returns false
        Reminder differentReminder = new ReminderBuilder().withTask("differentName").build();
        assertFalse(reminderCard.equals(new ReminderCard(differentReminder, 0)));

        // same reminder, different index -> returns false
        assertFalse(reminderCard.equals(new ReminderCard(reminder, 1)));
    }

    /**
     * Asserts that {@code reminderCard} displays the details of {@code expectedReminder} correctly and matches
     * {@code expectedId}.
     */
    private void assertCardDisplay(ReminderCard reminderCard, ReadOnlyReminder expectedReminder, int expectedId) {
        guiRobot.pauseForHuman();

        ReminderCardHandle reminderCardHandle = new ReminderCardHandle(reminderCard.getRoot());

        // verify id is displayed correctly
        assertEquals(Integer.toString(expectedId) + ". ", reminderCardHandle.getId());

        // verify reminder details are displayed correctly
        assertCardDisplaysReminder(expectedReminder, reminderCardHandle);
    }
}
```
###### \java\seedu\address\ui\ReminderListPanelTest.java
``` java

package seedu.address.ui;

import static org.junit.Assert.assertEquals;
import static seedu.address.testutil.EventsUtil.postNow;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_REMINDER;
import static seedu.address.testutil.TypicalReminders.getTypicalReminders;
import static seedu.address.ui.testutil.GuiTestAssert.assertCardDisplaysReminder;
import static seedu.address.ui.testutil.GuiTestAssert.assertReminderCardEquals;

import org.junit.Before;
import org.junit.Test;

import guitests.guihandles.ReminderCardHandle;
import guitests.guihandles.ReminderListPanelHandle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.events.ui.JumpToReminderRequestEvent;
import seedu.address.model.reminder.ReadOnlyReminder;

public class ReminderListPanelTest extends GuiUnitTest {
    private static final ObservableList<ReadOnlyReminder> TYPICAL_REMINDERS =
            FXCollections.observableList(getTypicalReminders());

    private static final JumpToReminderRequestEvent JUMP_TO_SECOND_EVENT = new JumpToReminderRequestEvent(
            INDEX_SECOND_REMINDER);

    private ReminderListPanelHandle reminderListPanelHandle;

    @Before
    public void setUp() {
        ReminderListPanel reminderListPanel = new ReminderListPanel(TYPICAL_REMINDERS);
        uiPartRule.setUiPart(reminderListPanel);

        reminderListPanelHandle = new ReminderListPanelHandle(getChildNode(reminderListPanel.getRoot(),
                ReminderListPanelHandle.REMINDER_LIST_VIEW_ID));
    }

    @Test
    public void display() {
        for (int i = 0; i < TYPICAL_REMINDERS.size(); i++) {
            reminderListPanelHandle.navigateToCard(TYPICAL_REMINDERS.get(i));
            ReadOnlyReminder expectedReminder = TYPICAL_REMINDERS.get(i);
            ReminderCardHandle actualCard = reminderListPanelHandle.getReminderCardHandle(i);

            assertCardDisplaysReminder(expectedReminder, actualCard);
            assertEquals(Integer.toString(i + 1) + ". ", actualCard.getId());
        }
    }

    @Test
    public void handleJumpToReminderRequestEvent() {
        postNow(JUMP_TO_SECOND_EVENT);
        guiRobot.pauseForHuman();

        ReminderCardHandle expectedCard = reminderListPanelHandle.getReminderCardHandle(
                INDEX_SECOND_REMINDER.getZeroBased());
        ReminderCardHandle selectedCard = reminderListPanelHandle.getHandleToSelectedCard();
        assertReminderCardEquals(expectedCard, selectedCard);
    }
}
```
###### \java\seedu\address\ui\testutil\GuiTestAssert.java
``` java
    /**
     * Asserts that {@code actualCard} displays the same values as {@code expectedCard}.
     */
    public static void assertReminderCardEquals(ReminderCardHandle expectedCard, ReminderCardHandle actualCard) {
        assertEquals(expectedCard.getId(), actualCard.getId());
        assertEquals(expectedCard.getTask(), actualCard.getTask());
        assertEquals(expectedCard.getPriority(), actualCard.getPriority());
        assertEquals(expectedCard.getDate(), actualCard.getDate());
        assertEquals(expectedCard.getMessage(), actualCard.getMessage());
        assertEquals(expectedCard.getTags(), actualCard.getTags());
    }
```
###### \java\seedu\address\ui\testutil\GuiTestAssert.java
``` java
    /**
     * Asserts that {@code actualCard} displays the details of {@code expectedReminder}.
     */
    public static void assertCardDisplaysReminder(ReadOnlyReminder expectedReminder, ReminderCardHandle actualCard) {
        assertEquals(expectedReminder.getTask().taskName, actualCard.getTask());
        assertEquals(expectedReminder.getPriority().value, actualCard.getPriority());
        assertEquals(expectedReminder.getDate().date, actualCard.getDate());
        assertEquals(expectedReminder.getMessage().message, actualCard.getMessage());
        assertEquals(expectedReminder.getTags().stream().map(tag -> tag.tagName).collect(Collectors.toList()),
                actualCard.getTags());
    }
```
###### \java\systemtests\TagCommandSystemTest.java
``` java

package systemtests;

import static seedu.address.logic.commands.TagCommand.MESSAGE_INVALID_INDEXES;
import static seedu.address.logic.commands.TagCommand.MESSAGE_PERSONS_ALREADY_HAVE_TAG;
import static seedu.address.logic.commands.TagCommand.MESSAGE_SUCCESS;
import static seedu.address.model.tag.Tag.MESSAGE_TAG_CONSTRAINTS;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.KEYWORD_MATCHING_MEIER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.junit.Test;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class TagCommandSystemTest extends AddressBookSystemTest {

    @Test
    public void tag() throws Exception {
        Model model = getModel();

        /* ----------------- Performing tag operation while an unfiltered list is being shown ---------------------- */

        /* Case: tag persons in address book, command with leading spaces and trailing spaces
         * and multiple spaces between each argument
         * -> tagged
         */
        Index indexOne = INDEX_FIRST_PERSON;
        Index indexTwo = INDEX_SECOND_PERSON;
        Tag tag = new Tag("tagTester");
        String command = " " + TagCommand.COMMAND_WORD + "  " + indexOne.getOneBased() + "," + indexTwo.getOneBased()
                + " " + "tagTester" + " ";
        Person firstTaggedPerson = new PersonBuilder(ALICE).withTags("friends", "retrieveTester", "tagTester").build();
        Person secondTaggedPerson = new PersonBuilder(BENSON).withTags("owesMoney", "friends",
                "retrieveTester", "tagTester").build();
        assertCommandSuccess(command, Arrays.asList(indexOne, indexTwo),
                tag, Arrays.asList(firstTaggedPerson, secondTaggedPerson));

        /* Case: undo editing the last person in the list -> last person restored */
        command = UndoCommand.COMMAND_WORD;
        String expectedResultMessage = UndoCommand.MESSAGE_SUCCESS;
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: redo editing the last person in the list -> last person edited again */
        command = RedoCommand.COMMAND_WORD;
        expectedResultMessage = RedoCommand.MESSAGE_SUCCESS;
        model.updatePerson(
                getModel().getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()), firstTaggedPerson);
        model.updatePerson(
                getModel().getFilteredPersonList().get(INDEX_SECOND_PERSON.getZeroBased()), secondTaggedPerson);
        assertCommandSuccess(command, model, expectedResultMessage);

        /* Case: tag persons, some of whom already have the tag in address book,
         * command with leading spaces and trailing spaces and multiple spaces between each argument
         * -> tagged
         */
        tag = new Tag("owesMoney");
        command = " " + TagCommand.COMMAND_WORD + "  " + indexOne.getOneBased() + "," + indexTwo.getOneBased()
                + " " + "owesMoney" + " ";
        firstTaggedPerson = new PersonBuilder(ALICE).withTags("friends", "retrieveTester",
                "tagTester", "owesMoney").build();
        secondTaggedPerson = new PersonBuilder(BENSON).withTags("owesMoney", "friends",
                "retrieveTester", "tagTester").build();
        assertCommandSuccess(command, Arrays.asList(indexOne, indexTwo),
                tag, Arrays.asList(firstTaggedPerson, secondTaggedPerson));

        /* ------------------ Performing tag operation while a filtered list is being shown ------------------------ */

        /* Case: filtered person list, tag index within bounds of address book and person list -> tagged */
        // all persons tagged
        showPersonsWithName(KEYWORD_MATCHING_MEIER);
        tag = new Tag("tagTesterNo2");
        command = " " + TagCommand.COMMAND_WORD + "  " + indexOne.getOneBased() + "," + indexTwo.getOneBased()
                + " " + "tagTesterNo2" + " ";
        firstTaggedPerson = new PersonBuilder(BENSON).withTags("owesMoney", "friends",
                "retrieveTester", "tagTester", "tagTesterNo2").build();
        secondTaggedPerson = new PersonBuilder(DANIEL).withTags("friends", "tagTesterNo2").build();
        assertCommandSuccess(command, Arrays.asList(indexOne, indexTwo),
                tag, Arrays.asList(firstTaggedPerson, secondTaggedPerson));

        // some persons already have the tag
        showPersonsWithName(KEYWORD_MATCHING_MEIER);
        tag = new Tag("owesMoney");
        command = " " + TagCommand.COMMAND_WORD + "  " + indexOne.getOneBased() + "," + indexTwo.getOneBased()
                + " " + "owesMoney" + " ";
        firstTaggedPerson = new PersonBuilder(BENSON).withTags("owesMoney", "friends",
                "retrieveTester", "tagTester", "tagTesterNo2").build();
        secondTaggedPerson = new PersonBuilder(DANIEL).withTags("friends", "tagTesterNo2", "owesMoney").build();
        assertCommandSuccess(command, Arrays.asList(indexOne, indexTwo),
                tag, Arrays.asList(firstTaggedPerson, secondTaggedPerson));

        /* Case: filtered person list, tag index within bounds of address book but out of bounds of person list
         * -> rejected
         */
        showPersonsWithName(KEYWORD_MATCHING_MEIER);
        int invalidIndex = getModel().getAddressBook().getPersonList().size();
        assertCommandFailure(TagCommand.COMMAND_WORD + " " + indexOne.getOneBased() + ","
                + invalidIndex + " " + "dummyTag", MESSAGE_INVALID_INDEXES);

        /* --------------------------------- Performing invalid edit operation -------------------------------------- */

        /* Case: invalid index (0) -> rejected */
        assertCommandFailure(TagCommand.COMMAND_WORD + " " + indexOne.getOneBased() + "," + 0 + " "
                + "dummyTag", String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));
        /* Case: invalid index (-1) -> rejected */
        assertCommandFailure(TagCommand.COMMAND_WORD + " " + indexOne.getOneBased() + "," + -1 + " "
                + "dummyTag", String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));

        /* Case: invalid index (size + 1) -> rejected */
        invalidIndex = getModel().getFilteredPersonList().size() + 1;
        assertCommandFailure(TagCommand.COMMAND_WORD + " " + indexOne.getOneBased() + "," + invalidIndex + " "
                + "dummyTag", MESSAGE_INVALID_INDEXES);

        /* Case: missing index -> rejected */
        assertCommandFailure(TagCommand.COMMAND_WORD + " " + "dummyTag",
                String.format(Messages.MESSAGE_INVALID_COMMAND_FORMAT, TagCommand.MESSAGE_USAGE));

        /* Case: invalid tag name -> rejected */
        assertCommandFailure(TagCommand.COMMAND_WORD + " " + indexOne.getOneBased() + ","
                + indexTwo.getOneBased() + " " + "!@#$", MESSAGE_TAG_CONSTRAINTS);
    }

    /**
     * Performs the same verification as {@code assertCommandSuccess(String, Model, String, Index)} and in addition,<br>
     * 1. Asserts that result display box displays the success message of executing {@code TagCommand}.<br>
     * 2. Asserts that the model related components are updated to reflect the person at indexes {@code targetIndexes}
     * being updated to values specified {@code taggedPersons}.<br>
     * @param targetIndexes the indexes of the current model's filtered list.
     * @see TagCommandSystemTest#assertCommandSuccess(String, Model, String)
     */
    private void assertCommandSuccess(String command, List<Index> targetIndexes,
                                      Tag tag, List<ReadOnlyPerson> taggedPersons) {
        Model expectedModel = getModel();
        List<ReadOnlyPerson> alreadyTaggedPersons = new ArrayList<>();
        List<ReadOnlyPerson> toBeTaggedPersons = new ArrayList<>();
        try {
            for (int i = 0; i < targetIndexes.size(); i++) {
                ReadOnlyPerson person = expectedModel.getFilteredPersonList().get(
                        targetIndexes.get(i).getZeroBased());
                if (person.getTags().contains(tag)) {
                    alreadyTaggedPersons.add(person);
                    continue;
                }
                toBeTaggedPersons.add(person);
                expectedModel.updatePerson(person, taggedPersons.get(i));
            }
        } catch (DuplicatePersonException | PersonNotFoundException e) {
            throw new IllegalArgumentException(
                    "taggedPerson is a duplicate in expectedModel, or it isn't found in the model.");
        }

        StringJoiner toBeTaggedJoiner = new StringJoiner(", ");
        for (ReadOnlyPerson person : toBeTaggedPersons) {
            toBeTaggedJoiner.add(person.getName().toString());
        }
        if (alreadyTaggedPersons.size() > 0) {
            StringJoiner alreadyTaggedJoiner = new StringJoiner(", ");
            for (ReadOnlyPerson person : alreadyTaggedPersons) {
                alreadyTaggedJoiner.add(person.getName().toString());
            }
            assertCommandSuccess(command, expectedModel, String.format(MESSAGE_SUCCESS,
                    targetIndexes.size() - alreadyTaggedPersons.size(), tag.toString()) + " "
                    + toBeTaggedJoiner.toString() + "\n"
                    + String.format(MESSAGE_PERSONS_ALREADY_HAVE_TAG, alreadyTaggedPersons.size()) + " "
                    + alreadyTaggedJoiner.toString());
        } else {
            assertCommandSuccess(command, expectedModel, String.format(MESSAGE_SUCCESS,
                    targetIndexes.size(), tag.toString()) + " " + toBeTaggedJoiner.toString());
        }
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays an empty string.<br>
     * 2. Asserts that the result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to {@code expectedModel}.<br>
     * 4. Asserts that the browser url and selected card update accordingly depending on the card at
     * {@code expectedSelectedCardIndex}.<br>
     * 5. Asserts that the status bar's sync status changes.<br>
     * 6. Asserts that the command box has the default style class.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     * @see AddressBookSystemTest#assertSelectedCardChanged(Index)
     */
    private void assertCommandSuccess(String command, Model expectedModel, String expectedResultMessage) {
        executeCommand(command);
        assertApplicationDisplaysExpected("", expectedResultMessage, expectedModel);
        assertCommandBoxShowsDefaultStyle();
        assertStatusBarUnchangedExceptSyncStatus();
    }

    /**
     * Executes {@code command} and in addition,<br>
     * 1. Asserts that the command box displays {@code command}.<br>
     * 2. Asserts that result display box displays {@code expectedResultMessage}.<br>
     * 3. Asserts that the model related components equal to the current model.<br>
     * 4. Asserts that the browser url, selected card and status bar remain unchanged.<br>
     * 5. Asserts that the command box has the error style.<br>
     * Verifications 1 to 3 are performed by
     * {@code AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)}.<br>
     * @see AddressBookSystemTest#assertApplicationDisplaysExpected(String, String, Model)
     */
    private void assertCommandFailure(String command, String expectedResultMessage) {
        Model expectedModel = getModel();

        executeCommand(command);
        assertApplicationDisplaysExpected(command, expectedResultMessage, expectedModel);
        assertSelectedCardUnchanged();
        assertCommandBoxShowsErrorStyle();
        assertStatusBarUnchanged();
    }

}
```
