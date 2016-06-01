package com.kovyazin.electric_emulator.request_handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public interface RequestHandler {
    byte[] handle(List<Integer> inputBytes) throws IOException, ClassNotFoundException, SQLException;
}
