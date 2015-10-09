package com.kovyazin.electric_emulator.request_handler;

import java.util.List;


public interface RequestHandler {
    byte[] handle(List<Integer> inputBytes);
}
