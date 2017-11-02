package seedu.address.logic.parser;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.commons.core.Messages.MESSAGE_UNKNOWN_COMMAND;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.AddReminderCommand;
import seedu.address.logic.commands.BirthdayCommand;
import seedu.address.logic.commands.ClearCommand;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.commands.DeleteReminderCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditReminderCommand;
import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.commands.FindEmailCommand;
import seedu.address.logic.commands.FindPhoneCommand;
import seedu.address.logic.commands.FindPriorityCommand;
import seedu.address.logic.commands.FindReminderCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListReminderCommand;
import seedu.address.logic.commands.LoginCommand;
import seedu.address.logic.commands.LogoutCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.RetagCommand;
import seedu.address.logic.commands.RetrieveCommand;
import seedu.address.logic.commands.SelectCommand;
import seedu.address.logic.commands.SortAgeCommand;
import seedu.address.logic.commands.SortBirthdayCommand;
import seedu.address.logic.commands.SortCommand;
import seedu.address.logic.commands.SortPriorityCommand;
import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.commands.UndoCommand;
import seedu.address.logic.commands.UntagCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses user input.
 */
public class AddressBookParser {

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     * @throws ParseException if the user input does not conform the expected format
     */
    public Command parseCommand(String userInput) throws ParseException {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return new AddCommandParser().parse(arguments);

        case BirthdayCommand.COMMAND_WORD:
            return new BirthdayCommandParser().parse(arguments);

        case EditCommand.COMMAND_WORD:
            return new EditCommandParser().parse(arguments);

        case LoginCommand.COMMAND_WORD:
            return new LoginCommandParser().parse(arguments);

        case LogoutCommand.COMMAND_WORD:
            return new LogoutCommand();

        case SelectCommand.COMMAND_WORD:
            return new SelectCommandParser().parse(arguments);

        case DeleteCommand.COMMAND_WORD:
            return new DeleteCommandParser().parse(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case ClearCommand.SECONDARY_COMMAND_WORD:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return new FindCommandParser().parse(arguments);

        case FindEmailCommand.COMMAND_WORD:
            return new FindEmailCommandParser().parse(arguments);

        case FindPhoneCommand.COMMAND_WORD:
            return new FindPhoneCommandParser().parse(arguments);

        case RetrieveCommand.COMMAND_WORD:
            return new RetrieveCommandParser().parse(arguments);

        case TagCommand.COMMAND_WORD:
            return new TagCommandParser().parse(arguments);

        case UntagCommand.COMMAND_WORD:
            return new UntagCommandParser().parse(arguments);

        case RetagCommand.COMMAND_WORD:
            return new RetagCommandParser().parse(arguments);

        case ListCommand.COMMAND_WORD:
            return new ListCommand();

        case HistoryCommand.COMMAND_WORD:
            return new HistoryCommand();

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case UndoCommand.COMMAND_WORD:
            return new UndoCommand();

        case RedoCommand.COMMAND_WORD:
            return new RedoCommand();

        case SortCommand.COMMAND_WORD:
            return new SortCommand();

        case SortAgeCommand.COMMAND_WORD:
            return new SortAgeCommand();

        case SortBirthdayCommand.COMMAND_WORD:
            return new SortBirthdayCommand();

        case AddReminderCommand.COMMAND_WORD:
            return new AddReminderCommandParser().parse(arguments);

        case DeleteReminderCommand.COMMAND_WORD:
            return new DeleteReminderCommandParser().parse(arguments);

        case EditReminderCommand.COMMAND_WORD:
            return new EditReminderCommandParser().parse(arguments);

        case FindPriorityCommand.COMMAND_WORD:
            return new FindPriorityCommandParser().parse(arguments);

        case FindReminderCommand.COMMAND_WORD:
            return new FindReminderCommandParser().parse(arguments);

        case ListReminderCommand.COMMAND_WORD:
            return new ListReminderCommand();

        case SortPriorityCommand.COMMAND_WORD:
            return new SortPriorityCommand();

        default:
            throw new ParseException(MESSAGE_UNKNOWN_COMMAND);
        }
    }

}
