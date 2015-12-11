package com.github.akarnokd.msmr.model;

import java.util.*;
import java.util.function.Supplier;

import hu.akarnokd.xml.*;
import it.unimi.dsi.fastutil.shorts.*;

public class Model implements XSerializable {

    public final Short2ObjectMap<Employee> employees = new Short2ObjectOpenHashMap<>();
    
    public final Short2ObjectMap<Machine> machines = new Short2ObjectOpenHashMap<>();
    
    public final Short2ObjectMap<Operation> operations = new Short2ObjectOpenHashMap<>();
    
    public final Short2ObjectMap<Station> stations = new Short2ObjectOpenHashMap<>();
    
    public final Short2ObjectMap<Routing> routings = new Short2ObjectOpenHashMap<>();
    
    public final Short2ObjectMap<ShortSet> operationMachines = new Short2ObjectOpenHashMap<>();
    
    public final Short2ObjectMap<Product> products = new Short2ObjectOpenHashMap<>();
    
    public final List<Product> productIn = new ArrayList<>();
    public final List<Product> productOut = new ArrayList<>();

    
    static <T extends XSerializable & ShortID> void load(XElement source, String groupName, 
            String itemName, Supplier<T> instance,
            Short2ObjectMap<T> map) {
        map.clear();
        
        for (XElement xs : source.childrenWithName(groupName)) {
            for (XElement x : xs.childrenWithName(itemName)) {
                T item = instance.get();
                item.load(x);
                map.put(item.id(), item);
            }
        }
    }
    
    static <T extends XSerializable> void save(XElement xout, String groupName, 
            String itemName, Short2ObjectMap<T> map) {
        XElement xgr = xout.add(groupName);
        
        map.values().forEach(v -> {
            XElement xi = xgr.add(itemName);
            v.save(xi);
        });
    }

    @Override
    public void load(XElement source) {
        load(source, "employees", "employee", Employee::new, employees);
        load(source, "machines", "machine", Machine::new, machines);
        load(source, "operations", "operation", Operation::new, operations);
        load(source, "stations", "station", Station::new, stations);
        load(source, "routings", "routing", Routing::new, routings);
        
        operations.values().forEach(op -> op.resolve(operations));
        routings.values().forEach(rt -> rt.resolve(operations));
    }

    @Override
    public void save(XElement destination) {
        save(destination, "employees", "employee", employees);
        save(destination, "machines", "machine", machines);
        save(destination, "operations", "operation", operations);
        save(destination, "stations", "station", stations);
        save(destination, "routings", "routing", routings);
    }
}
