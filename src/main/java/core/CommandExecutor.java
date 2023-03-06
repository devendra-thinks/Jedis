package core;

public class CommandExecutor {
    public static CommandExecutor instance = new CommandExecutor();
    private CommandExecutor(){}
    public Object  execute(RedisCommand redisCommand){
        if(redisCommand.command == Command.PING){
            if(redisCommand.args.length == 0)
               return "+PONG\r\n";
            else{
                return "+" + redisCommand.args[0] +"\r\n";
            }
        }
        return "-Error occured\r\n";
    }
}
