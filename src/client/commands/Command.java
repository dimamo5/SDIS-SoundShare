package client.commands;

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
        LOGOUT
    }

    protected Type type;
    protected String[] args;

    public Command(String command) throws CommandException {
        parseCommand(command);
    }

    public void parseCommand(String command) throws CommandException {
        String[] args = command.split("\\s+");

        this.type = parseType(args[0]);
        this.args = new String[args.length - 1];
        System.arraycopy(args, 1, this.args, 0, args.length);

        parseArgs(type);
    }

    private boolean parseArgs(Type type) throws CommandException {
        //// TODO: 25/05/2016  verificação semantica nos argumentos Ex: A porta nao pode ser "ABC" tem de ser [0-9]+
        switch (type) {
            case SKIP:
                return validate_args_length(1);

            case REQUEST:
                return validate_args_length(2);

            case LOGOUT:
                return validate_args_length(1);

            case CONNECT_ROOM:
                return validate_args_length(2);

            case DISCONNECT_ROOM:
                return validate_args_length(1);

            case CREATE_ROOM:
                return validate_args_length(1);

            default:
                throw new CommandException("Invalid command type");
        }
    }

    public boolean validate_args_length(int no_args_expected) throws CommandException {
        if (args.length != no_args_expected) {
            throw new CommandException("Number of arguments expected: " + no_args_expected + ". Given: " + (args.length - 1));
        }
        return true;
    }


    public Type parseType(String type) throws CommandException {
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
