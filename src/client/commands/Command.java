package client.commands;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by duarte on 24-05-2016.
 */
public class Command {
    public enum Type {
        CONNECT_ROOM,
        DISCONNECT_ROOM,
        CREATE_ROOM,
        SKIP,
        REQUEST,
        LOGOUT,
        ROOM_LIST
    }

    protected Type type;
    protected String[] args;

    public Command(String command) throws CommandException {
        parseCommand(command);
    }

    private void parseCommand(String command) throws CommandException {
        String[] args = command.split("\\s+");

        this.type = parseType(args[0]);
        if (args.length > 1) {
            this.args = new String[args.length - 1];
            parseArgs(type, args);
            System.arraycopy(args, 1, this.args, 0, args.length - 1);
        }
    }

    private boolean parseArgs(Type type, String[] args) throws CommandException {
        switch (type) {
            case SKIP:
                return validateArgsLength(0, args);
            case REQUEST:
                return validateArgsLength(2, args) && ("true".equalsIgnoreCase(args[2]) || "false".equalsIgnoreCase(args[2]));
            case LOGOUT:
                return validateArgsLength(0, args);
            case CONNECT_ROOM:
                if(!validateArgsLength(1, args))
                    return false;
                if (!NumberUtils.isNumber(args[1]))
                    throw new CommandException("Argument number 1, port, must be an integer");
                else{
                    return true;
                }
            case DISCONNECT_ROOM:
                return validateArgsLength(0, args);
            case CREATE_ROOM:
                return validateArgsLength(0, args);
            case ROOM_LIST:
                return validateArgsLength(0, args);
            default:
                throw new CommandException("Invalid command type");
        }
    }

    private boolean validateArgsLength(int numArgsExpected, String[] args) throws CommandException {
        if ((args.length - 1) != numArgsExpected) {
            throw new CommandException("Number of arguments expected: " + numArgsExpected + ". Given: " + (args.length - 1));
        }
        return true;
    }


    private Type parseType(String type) throws CommandException {
        for (Type enumType : Command.Type.values()) {
            if (type.equalsIgnoreCase(enumType.toString()))
                return enumType;
        }
        throw new CommandException("Invalid command type!");
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
