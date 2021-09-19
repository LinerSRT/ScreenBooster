package com.liner.screenboster.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.List;

public class Shell {

    public static boolean haveRoot(){
        return exec("echo root", true).isSuccess();
    }

    public static Result exec(String command) {
        return exec(new String[]{command});
    }

    public static Result exec(String command, boolean requireRoot) {
        return exec(new String[]{command}, requireRoot);
    }

    public static Result exec(List<String> commandList, boolean requireRoot) {
        return exec(commandList.toArray(new String[]{}), requireRoot);
    }

    public static Result exec(String... commandList) {
        return exec(commandList, false);
    }

    public static Result exec(boolean requireRoot, String... commandList) {
        return exec(commandList, requireRoot);
    }

    public static Result exec(String[] commandList, boolean requireRoot) {
        int result = -1;
        if (commandList == null || commandList.length == 0) {
            return new Result(result, null);
        }
        try {
            Result commandResult;
            Process process = Runtime.getRuntime().exec(requireRoot ? "su" : "sh");
            DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
            for (String command : commandList) {
                if (command == null)
                    continue;
                dataOutputStream.write(command.getBytes());
                dataOutputStream.writeBytes("\n");
                dataOutputStream.flush();
            }
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            result = process.waitFor();
            StringBuilder successMsg = new StringBuilder();
            StringBuilder errorMsg = new StringBuilder();
            BufferedReader successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String line;
            while ((line = successResult.readLine()) != null) {
                successMsg.append(line);
            }
            while ((line = errorResult.readLine()) != null) {
                errorMsg.append(line);
            }
            successResult.close();
            errorResult.close();
            commandResult = new Result(result, successMsg.toString().length() == 0 ? errorMsg.toString() : successMsg.toString());
            process.destroy();
            dataOutputStream.close();
            return commandResult;
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(-1, e.getMessage());
        }
    }

    public static class Result {
        private final int result;
        private final String response;

        public Result(int result, String response) {
            this.result = result;
            this.response = response;
        }

        public boolean isSuccess() {
            return result == 0;
        }

        public int getResult() {
            return result;
        }

        public String getResponse() {
            return response;
        }

        @Override
        public String toString() {
            return "Result{" +
                    "result=" + result +
                    ", response='" + response + '\'' +
                    '}';
        }
    }
}
