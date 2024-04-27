package com.cmu.webappbff;

import com.cmu.webappbff.controller.BookBffController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

public class JWTHelper {
  private static final Logger logger = LoggerFactory.getLogger(BookBffController.class);

  private static final String ISSUER = "cmu.edu";
  private static final String[] VALID_USERS = {"starlord", "gamora", "drax", "rocket", "groot"};

  static String extractJWTToken(String tokenWithPrefix) {
    return tokenWithPrefix.replace("Bearer ", "");
  }

  public static boolean verifyJWTToken(String token) {
    String jwtToken = extractJWTToken(token);
    String[] chunks = jwtToken.split("\\.");
    Base64.Decoder decoder = Base64.getUrlDecoder();

    String payload = new String(decoder.decode(chunks[1]));
    logger.info("jwt payload received: " + payload);
    System.out.println("Payload: " + payload);

    // if payload is empty
    if (StringUtils.isEmpty(payload)) return false;

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = null;
    try {
      jsonNode = objectMapper.readTree(payload);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }

    if (ObjectUtils.isEmpty(jsonNode.get("sub"))
        || ObjectUtils.isEmpty(jsonNode.get("exp"))
        || ObjectUtils.isEmpty(jsonNode.get("iss"))) return false;
    String subClaim = jsonNode.get("sub").asText();
    long expClaim = jsonNode.get("exp").asLong();
    String issClaim = jsonNode.get("iss").asText();
    logger.info("subclaim received: " + subClaim);
    logger.info("exp: " + expClaim);
    logger.info("issClaim: " + issClaim);

    // return false if sub or iss is not present
    if (StringUtils.isAnyEmpty(subClaim, issClaim)) return false;

    // Verify 'sub' claim
    if (!isValidUser(subClaim)) {
      System.out.println("Invalid 'sub' claim: " + subClaim);
      return false;
    }

    // Verify 'exp' claim

    long currentTimestamp = System.currentTimeMillis() / 1000; // Convert to seconds
    logger.info("Current time: " + currentTimestamp);
    logger.info("Received timestamp: " + expClaim);
    if (expClaim <= currentTimestamp) {
      System.out.println("Token has expired");
      return false;
    }

    // Verify 'iss' claim
    if (!ISSUER.equalsIgnoreCase(issClaim)) {
      System.out.println("Invalid 'iss' claim: " + issClaim);
      return false;
    }

    System.out.println("JWT token is valid");
    return true;
  }

  private static boolean isValidUser(String subClaim) {
    for (String user : VALID_USERS) {
      if (user.equalsIgnoreCase(subClaim)) {
        return true;
      }
    }
    return false;
  }
}
