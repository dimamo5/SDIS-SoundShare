package client.commands;

/**
 * Created by duarte on 24-05-2016.
 */
public class Command {
    public enum Type{
        SKIP,
        REQUEST
    }

    protected Type type;
    protected String[] args;

    public Command(String command) throws CommandException {
        parseCommand(command);
    }

    public void parseCommand(String command) throws CommandException {
        String[] args = command.split("\\s+");
        Type type = parseType(args[0]);
        parseArgs(type,args);
        this.type = type;
        this.args = new String[args.length-1];
        System.arraycopy(args,1,this.args,0,args.length);
    }

    private boolean parseArgs(Type type, String[] args) throws CommandException {
        switch (type){
            case SKIP:
                if(args.length !=1){
                    throw new CommandException("Number of arguments expected: 0. Given: "+(args.length-1));
                }
                return true;
            case REQUEST:
                if(args.length != 2){
                    throw new CommandException("Number of arguments expected: 1. Given: "+(args.length-1));
                }
                return true;
            default:
                throw new CommandException("Invalid command type");
        }
    }

    public Type parseType(String type) throws CommandException {
        if(type.equalsIgnoreCase(Type.SKIP.toString())){
            return Type.SKIP;
        }else if(type.equalsIgnoreCase(Type.REQUEST.toString())){
            return Type.REQUEST;
        }else{
            throw new CommandException("Invalid command type!");
        }
    }
}
