package duke;

import duke.commands.*;
import duke.exceptions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Parser {

    public Parser() {}

    public static Command parse(String fullCommand) {
        String[] commandParts = fullCommand.split(" ", 2);
        String commandHeader = commandParts[0];
        switch (commandHeader) {
            case "bye":
                return new ExitCommand();
            case "list":
                return new ListCommand();
            case "todo":
                return parseTodoCommand(fullCommand);
            case "deadline":
                return parseDeadlineCommand(fullCommand);
            case "event":
                return parseEventCommand(fullCommand);
            case "mark":
                return parseMarkCommand(fullCommand);
            case "unmark":
                return parseUnmarkCommand(fullCommand);
            case "delete":
                return parseDeleteCommand(fullCommand);
            default:
                return new InvalidCommand();
        }
    }

    private static AddTodoCommand parseTodoCommand(String fullCommand) throws TaskNoDescriptionException {
        String info = fullCommand.substring(4).trim();
        if (info.isEmpty()) {
            throw (new TaskNoDescriptionException("☹ OOPS!!! The description of a todo cannot be empty."));
        }

        return new AddTodoCommand(info);
    }

    private static AddDeadlineCommand parseDeadlineCommand(String fullCommand)
        throws TaskNoDescriptionException, NotEnoughArgumentsException, DateTimeParseException {
        String info = fullCommand.substring(8).trim();
        if (info.isEmpty()) {
            throw(new TaskNoDescriptionException("☹ OOPS!!! The description of a deadline cannot be empty."));
        }

        try {
            String[] infoParts = info.split("/", 2);
            String description = infoParts[0], by = infoParts[1].substring(2).trim();
            LocalDate byDate = LocalDate.parse(by, DateTimeFormatter.ofPattern("d/MM/yyyy"));
            return new AddDeadlineCommand(description, byDate);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw(new NotEnoughArgumentsException("☹ OOPS!!! Deadline requires a date after the description."));
        }
    }

    private static AddEventCommand parseEventCommand(String fullCommand)
    throws TaskNoDescriptionException, NotEnoughArgumentsException,
            DateNotInSequenceException, DateTimeParseException {
        String info = fullCommand.substring(5).trim();
        if (info.isEmpty()) {
            throw (new TaskNoDescriptionException("☹ OOPS!!! The description of an event cannot be empty."));
        }

        try {
            String[] infoParts = info.split(" /", 3);
            String description = infoParts[0],
                    from = infoParts[1].substring(4).trim(),
                    to = infoParts[2].substring(2).trim();
            LocalDate fromDate = LocalDate.parse(from, DateTimeFormatter.ofPattern("d/MM/yyyy")),
                    toDate = LocalDate.parse(to, DateTimeFormatter.ofPattern("d/MM/yyyy"));
            if (toDate.isBefore(fromDate)) {
                throw new DateNotInSequenceException("Event end date must be earlier than start date");
            }
            return new AddEventCommand(description, fromDate, toDate);
        } catch(ArrayIndexOutOfBoundsException e) {
            throw (new NotEnoughArgumentsException("☹ OOPS!!! Event requires a start time and an end time."));
        }
    }

    private static MarkCommand parseMarkCommand(String fullCommand) throws NotEnoughArgumentsException {
        String info = fullCommand.substring(4).trim();
        if (info.isEmpty()) {
            throw (new TaskNoDescriptionException("Please specify a task number to mark."));
        }
        int taskNumber = Integer.parseInt(info);
        return new MarkCommand(taskNumber, true);
    }

    private static MarkCommand parseUnmarkCommand(String fullCommand) throws NotEnoughArgumentsException {
        String info = fullCommand.substring(6).trim();
        if (info.isEmpty()) {
            throw (new TaskNoDescriptionException("Please specify a task number to unmark."));
        }
        int taskNumber = Integer.parseInt(info);
        return new MarkCommand(taskNumber, false);
    }

    private static DeleteCommand parseDeleteCommand(String fullCommand) throws NotEnoughArgumentsException {
        String info = fullCommand.substring(6).trim();
        if (info.isEmpty()) {
            throw (new TaskNoDescriptionException("Please specify a task number to delete."));
        }
        int taskNumber = Integer.parseInt(info);
        return new DeleteCommand(taskNumber);
    }
}