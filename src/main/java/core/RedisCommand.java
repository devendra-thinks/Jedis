package core;

public class RedisCommand {
    Command command;
    String []args;

    public RedisCommand(Command command, String []args){
        this.command = command;
        this.args = args;
    }
}
