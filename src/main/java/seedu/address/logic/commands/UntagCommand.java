//@@author duyson98

package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.ReadOnlyPerson;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;
import seedu.address.model.tag.Tag;
import seedu.address.model.tag.UniqueTagList;

/**
 * Untags one or more persons identified using their last displayed targetIndexes from the address book.
 */
public class UntagCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "untag";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Untags one or more persons in the last person listing.\n"
            + "- Untag all tags of persons identified by the index numbers used\n"
            + "Parameters: INDEX,[MORE_INDEXES]... (must be positive integers)\n"
            + "Example: " + COMMAND_WORD + " 1,2,3\n"
            + "- Untag one or more tags of persons identified by the index numbers used\n"
            + "Parameters: INDEX,[MORE_INDEXES]... (must be positive integers) + TAGNAME\n"
            + "Example: " + COMMAND_WORD + " 1,2,3 friends/colleagues\n"
            + "- Untag all tags of all persons in the last person listing\n"
            + "Parameters: -a\n"
            + "Example: " + COMMAND_WORD + " -a\n"
            + "- Untag one or more tags of all persons in the last person listing\n"
            + "Parameters: -a + TAGNAME\n"
            + "Example: " + COMMAND_WORD + " -a friends/colleagues";

    public static final String MESSAGE_SUCCESS = "%d person(s) successfully untagged from %s:";
    public static final String MESSAGE_SUCCESS_ALL_TAGS = "%d person(s) sucessfully untagged:";
    public static final String MESSAGE_SUCCESS_MULTIPLE_TAGS_IN_LIST = "%s tag(s) successfully removed.";
    public static final String MESSAGE_SUCCESS_ALL_TAGS_IN_LIST = "All tags in the list successfully removed.";

    public static final String MESSAGE_TAG_NOT_FOUND = "Tag not found.";
    public static final String MESSAGE_PERSONS_DO_NOT_HAVE_TAGS = "%d person(s) do not have any of the specified tags:";
    public static final String MESSAGE_EMPTY_INDEX_LIST = "Please provide one or more indexes! \n%1$s";
    public static final String MESSAGE_INVALID_INDEXES = "One or more person indexes provided are invalid.";
    public static final String MESSAGE_DUPLICATE_PERSON = "This person already exists in the address book.";

    private boolean toAllInFilteredList;
    private List<Index> targetIndexes;
    private List<Tag> tags;

    /**
     * @param targetIndexes of the persons in the filtered person list to untag
     * @param tags of the persons
     */
    public UntagCommand(boolean toAllInFilteredList, List<Index> targetIndexes, List<Tag> tags) {
        requireNonNull(targetIndexes);
        requireNonNull(tags);

        this.toAllInFilteredList = toAllInFilteredList;
        this.targetIndexes = targetIndexes;
        this.tags = tags;
    }

    @Override
    protected CommandResult executeUndoableCommand() throws CommandException {
        List<ReadOnlyPerson> lastShownList = model.getFilteredPersonList();

        if (toAllInFilteredList) {
            for (ReadOnlyPerson personToUntag : lastShownList) {
                removeTagsFromPerson(personToUntag, tags);
                deleteUnusedTagsInTagList(new ArrayList<>(personToUntag.getTags()));
            }
            return (tags.isEmpty()) ? new CommandResult(MESSAGE_SUCCESS_ALL_TAGS_IN_LIST)
                    : new CommandResult(String.format(MESSAGE_SUCCESS_MULTIPLE_TAGS_IN_LIST, joinList(tags)));
        }

        for (Index targetIndex : targetIndexes) {
            if (targetIndex.getZeroBased() >= lastShownList.size()) {
                throw new CommandException(MESSAGE_INVALID_INDEXES);
            }
        }

        ArrayList<Name> toBeUntaggedPersonNames = new ArrayList<>();
        if (tags.isEmpty()) {
            for (Index targetIndex : targetIndexes) {
                ReadOnlyPerson personToUntag = lastShownList.get(targetIndex.getZeroBased());
                removeTagsFromPerson(personToUntag, tags);
                toBeUntaggedPersonNames.add(personToUntag.getName());
                deleteUnusedTagsInTagList(new ArrayList<>(personToUntag.getTags()));
            }
            return new CommandResult(String.format(MESSAGE_SUCCESS_ALL_TAGS, targetIndexes.size()) + " "
                    + joinList(toBeUntaggedPersonNames));
        }

        ArrayList<Name> alreadyUntaggedPersonNames = new ArrayList<>();
        for (Index targetIndex : targetIndexes) {
            ReadOnlyPerson personToUntag = lastShownList.get(targetIndex.getZeroBased());
            if (Collections.disjoint(personToUntag.getTags(), tags)) {
                alreadyUntaggedPersonNames.add(personToUntag.getName());
                continue;
            }
            removeTagsFromPerson(personToUntag, tags);
            toBeUntaggedPersonNames.add(personToUntag.getName());
        }

        deleteUnusedTagsInTagList(tags);

        if (alreadyUntaggedPersonNames.size() > 0) {
            return new CommandResult(String.format(MESSAGE_SUCCESS,
                    targetIndexes.size() - alreadyUntaggedPersonNames.size(), joinList(tags)) + " "
                    + joinList(toBeUntaggedPersonNames) + "\n"
                    + String.format(MESSAGE_PERSONS_DO_NOT_HAVE_TAGS, alreadyUntaggedPersonNames.size()) + " "
                    + joinList(alreadyUntaggedPersonNames));
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS,
                targetIndexes.size(), joinList(tags)) + " " + joinList(toBeUntaggedPersonNames));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UntagCommand // instanceof handles nulls
                && this.targetIndexes.equals(((UntagCommand) other).targetIndexes))
                && this.tags.equals(((UntagCommand) other).tags); // state check
    }

    /**
     * Removes a tag from the person
     * Removes all tags if tag is not specified
     * @param person to be untagged
     * @param tags to be removed
     */
    private void removeTagsFromPerson(ReadOnlyPerson person, List<Tag> tags) throws CommandException {
        Person untaggedPerson = new Person(person);
        UniqueTagList updatedTags = new UniqueTagList();
        if (!tags.isEmpty()) {
            updatedTags = new UniqueTagList(person.getTags());
            for (Tag t : tags) {
                updatedTags.remove(t);
            }
        }
        untaggedPerson.setTags(updatedTags.toSet());

        try {
            model.updatePerson(person, untaggedPerson);
        } catch (DuplicatePersonException e) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        } catch (PersonNotFoundException e) {
            throw new AssertionError("The target person cannot be missing");
        }
    }

    private void deleteUnusedTagsInTagList(List<Tag> tags) {
        for (Tag tag : tags) {
            model.deleteUnusedTag(tag);
        }
    }

    /**
     * Join all list elements by commas
     * @param list to be joined
     */
    private String joinList(List list) {
        StringJoiner joiner = new StringJoiner(", ");
        for (Object obj : list) {
            joiner.add(obj.toString());
        }
        return joiner.toString();
    }

}
