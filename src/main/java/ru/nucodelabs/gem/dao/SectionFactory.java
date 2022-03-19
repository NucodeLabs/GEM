package ru.nucodelabs.gem.dao;

import ru.nucodelabs.data.ves.Picket;

import java.util.List;

public interface SectionFactory {
    Section create(List<Picket> picketList);
}
