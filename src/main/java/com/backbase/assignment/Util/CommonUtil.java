package com.backbase.assignment.Util;

import com.backbase.assignment.Model.ErrorResponse;
import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class CommonUtil {
    public static String generateUniqueGameId() {
        return Constants.GAME_ID_PREFIX + Long.toString(UUID.randomUUID().getLeastSignificantBits());
    }

    public static ResponseEntity createOkResponse(Pair<String, Object>... pairs) {
        JSONObject resultJson = new JSONObject();
        for (Pair<String, Object> p : pairs) {
            resultJson.put(p.getLeft(), p.getRight());
        }
        resultJson.put(Constants.API_STATUS, Constants.SUCCESS_STRING);
        return new ResponseEntity(resultJson.toString(), HttpStatus.OK);
    }

    public static ResponseEntity createFailureResponse(List<String> errorList, String errorMessage, HttpStatus httpStatus) {
        ErrorResponse errorResponse = new ErrorResponse(errorMessage, errorList, Constants.FAILURE_STRING);
        JSONObject jsonObject = new JSONObject(errorResponse);
        return new ResponseEntity(jsonObject.toString(), httpStatus);
    }

    public static String getLocalIP() {
//        try (final DatagramSocket socket = new DatagramSocket()) {
//            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
//            return socket.getLocalAddress().getHostAddress();
//        } catch (Exception e) {
//            return Constants.LOCALHOST_IP;
//        }
        return Constants.LOCALHOST_IP;
    }


}
