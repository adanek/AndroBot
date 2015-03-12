package at.uibk.informatik.androbot.contracts;

public interface IConnection {

     void write(byte[] message);
     void connect();
     void disconnect();     
}
