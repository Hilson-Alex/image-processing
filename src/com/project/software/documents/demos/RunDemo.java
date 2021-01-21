package com.project.software.documents.demos;

import com.project.software.documents.ImageHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class RunDemo {

    private Scanner scanner = new Scanner(System.in);

    private final String OPEN_PARENTHESES = "(";
    private final String CLOSE_PARENTHESES = ")";

    private final String VALUE = "value";
    private final String SRC = "src";
    private final String COMMAND = "command";

    private final String CLOSE_COMMAND = "exit";

    //private final Map<String, String> sources = new HashMap<>();

    private final ImageHandler source;

    public RunDemo (ImageHandler source){
        this.source = source;
    }

    /**
     * Menu loop
     */
    public void run() throws IOException {
        Boolean menuReturn;

        do {
            System.out.print("PI> : ");
            menuReturn = litemCommand();
        } while (menuReturn);
    }

    public String readString() {
        return scanner.nextLine();
    }

    /**
     * Call calculate methods
     * @return boolean, control while
     */
    @SuppressWarnings("unchecked")
    private boolean litemCommand() throws IOException{

        String commandFull = scanner.nextLine();
        Map commandValue = extractValues(commandFull.replaceAll("\\s+",""));

        try {
            Demos.valueOf(((String) commandValue.get(COMMAND)).toUpperCase())
                    .exec(source, (Optional<String>) commandValue.get(VALUE));
            source.clear();
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid Command, type \"help\" to show commands");
        }

        return !commandValue.get(COMMAND).equals(CLOSE_COMMAND);
    }


    private Map extractValues(String command) {
        var values = new HashMap<String, Object>();
        values.put(VALUE, Optional.empty());
        if (command.contains(OPEN_PARENTHESES)) {
            var indexStart = command.indexOf(OPEN_PARENTHESES);
            var indexEnd = command.indexOf(CLOSE_PARENTHESES);

            var valueString = command.substring(indexStart + 1, indexEnd);
            var commandString = command.substring(0, indexStart);

            if(!valueString.equals("")){
                values.put(VALUE, Optional.of(valueString));
            }
            values.put(COMMAND, commandString);

            return values;
        }

        values.put(COMMAND, command);

        return values;
    }

}
