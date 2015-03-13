package at.uibk.informatik.androbot.contracts;

import java.io.IOException;

public interface IConnection {

     void write(byte[] message);
     void connect() throws IOException;
     void disconnect();     
}
