package at.uibk.informatik.androbot.contracts;

public interface IConnection {

     void write(String message);
     void connect();
     void disconnect();
     
}
