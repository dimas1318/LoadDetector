package balancer.serializer;

import java.io.FileNotFoundException;

public interface KryoContext {

    void serialize(Object obj, String fileName) throws FileNotFoundException;

    Object deserialize(String fileName) throws FileNotFoundException;
}