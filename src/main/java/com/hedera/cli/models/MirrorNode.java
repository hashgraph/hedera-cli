package com.hedera.cli.models;

import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class MirrorNode {
    String name;
    String description;
    String address;
}