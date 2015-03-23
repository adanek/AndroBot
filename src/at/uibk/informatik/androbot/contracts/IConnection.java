package at.uibk.informatik.androbot.contracts;

import java.io.IOException;

public interface IConnection {

     void sendCommand(byte[] command);
     String getResponse(byte[] command);
     void connect() throws IOException;
     void disconnect();     
     boolean isConnected();
}
