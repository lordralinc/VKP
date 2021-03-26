package dev.idm.vkp.domain.mappers;

public interface MapF<O, R> {
    R map(O orig);
}