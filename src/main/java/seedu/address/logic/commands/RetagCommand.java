package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.ArrayList;
import java.util.List;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

/**
 * Replace an tag name in person list by a new tag name from the address book.
 */
public class RetagCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "retag";
    public static final String COMMAND_ALIAS = "rt";

    public static final String MESSAGE_USAGE = COMMAND_WORD
        + ": Retags all person having the old tag name to the new tag name.\n"
        + "Parameters: OLDTAGNAME + NEWTAGNAME\n"
        + "Example: " + COMMAND_WORD + " friends enemies";

    public static final String MESSAGE_SUCCESS = "%s tag in person list successfully replaced by %s.";

    public static final String MESSAGE_TAG_NOT_FOUND = "%s tag not found in person list.";
    public static final String MESSAGE_INVALID_ARGS = "Target tag name is the same as new tag name. \n%1$s";
    public static final String MESSAGE_DUPLICATE_TAG = "One or more persons already have this tag.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private Tag targetTag;
    private Tag newTag;

    /**
    * @param targetTag of persons in the filtered person list to retag
    * @param newTag of persons
    */
    public RetagCommand(Tag targetTag, Tag newTag) {
        requireNonNull(targetTag);
        requireNonNull(newTag);

        this.targetTag = targetTag;
        this.newTag = newTag;
    }

    @Override
    protected CommandResult executeUndoableCommand() throws CommandException {
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        //Todo: Tag not found should be checked in person list only
        if (!model.getAddressBook().getTagList().contains(targetTag)) {
            throw new CommandException(String.format(MESSAGE_TAG_NOT_FOUND, targetTag.toString()));
        }

        List<ReadOnlyPerson> lastShownListCopy = new ArrayList<>(model.getFilteredPersonList());

        for (ReadOnlyPerson person : lastShownListCopy) {
            Person retaggedPerson = new Person(person);
            UniqueTagList updatedTags = new UniqueTagList(retaggedPerson.getTags());
            if (updatedTags.contains(targetTag)) {
                updatedTags.remove(targetTag);
            } else {
                continue;
            }

            if (!updatedTags.contains(newTag)) {
                try {
                    updatedTags.add(newTag);
                } catch (UniqueTagList.DuplicateTagException e) {
                    throw new CommandException(MESSAGE_DUPLICATE_TAG);
                }
            }

            retaggedPerson.setTags(updatedTags.toSet());
            try {
                model.updatePerson(person, retaggedPerson);
            } catch (DuplicatePersonException dpe) {
                throw new CommandException(MESSAGE_DUPLICATE_PERSON);
            } catch (PersonNotFoundException pnfe) {
                throw new AssertionError("The target person cannot be missing");
            }
        }

        model.deleteUnusedTag(targetTag);
        return new CommandResult(String.format(MESSAGE_SUCCESS, targetTag.toString(), newTag.toString()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof RetagCommand // instanceof handles nulls
            && this.targetTag.equals(((RetagCommand) other).targetTag)) // state check
            && this.newTag.equals(((RetagCommand) other).newTag); // state check
    }

}
