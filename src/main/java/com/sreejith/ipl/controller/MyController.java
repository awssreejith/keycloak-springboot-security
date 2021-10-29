package com.sreejith.ipl.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/ipl")
public class MyController {

    @RolesAllowed("player")
    @GetMapping("/playerOnly")
    public ResponseEntity<?> playerOnly()
    {
        return ResponseEntity.status(HttpStatus.OK).body("Hello Player");
    }

    @RolesAllowed({ "matchreferee", "umpire" })
    @GetMapping("/umpireOnly")
    public ResponseEntity<?> umpireOnly()
    {
        return ResponseEntity.status(HttpStatus.OK).body("Hello Umpire or Match referee");
    }

    @RolesAllowed("matchreferee")
    @GetMapping("/matchRefereeOnly")
    public ResponseEntity<?> matchRefereeOnly()
    {
        return ResponseEntity.status(HttpStatus.OK).body("Hello Match referee");
    }

    @GetMapping("/all")
    public ResponseEntity<?> all()
    {
        return ResponseEntity.status(HttpStatus.OK).body("Hello all");
    }
}
