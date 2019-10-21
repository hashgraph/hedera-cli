package com.hedera.cli.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonMerge;

import lombok.Getter;
import lombok.Setter;

/**
 * AddressBook class manages the parsing of the addressbook.json file,
 * which is stored in resources directory
 */
@Getter
@Setter
public class AddressBook {

  @JsonMerge
  List<Network> networks;

}