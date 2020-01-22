package com.hedera.cli.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonMerge;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressBookMirror {

    @JsonMerge
    private List<MirrorNode> mirrorNodes;
    
}