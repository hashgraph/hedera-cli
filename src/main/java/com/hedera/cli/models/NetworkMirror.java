package com.hedera.cli.models;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NetworkMirror {

    private List<MirrorNode> mirrorNodes;

}