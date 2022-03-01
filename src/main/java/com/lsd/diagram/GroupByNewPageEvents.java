package com.lsd.diagram;

import com.lsd.events.Newpage;
import com.lsd.events.PageTitle;
import com.lsd.events.SequenceEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Splits a given List of sequence events into sublists if NewPage events are found. The NewPage event is converted to
 * a PageTitle event (preserving the title).
 */
class GroupByNewPageEvents implements Collector<SequenceEvent, List<List<SequenceEvent>>, List<List<SequenceEvent>>> {
    @Override
    public Supplier<List<List<SequenceEvent>>> supplier() {
        return () -> {
            var lists = new ArrayList<List<SequenceEvent>>();
            lists.add(new ArrayList<>());
            return lists;
        };
    }

    @Override
    public BiConsumer<List<List<SequenceEvent>>, SequenceEvent> accumulator() {
        return (lists, sequenceEvent) -> {
            if (sequenceEvent instanceof Newpage) {
                var newGrouping = new ArrayList<SequenceEvent>();
                newGrouping.add(PageTitle.titled(((Newpage) sequenceEvent).getTitle()));
                lists.add(newGrouping);
            } else {
                var currentGrouping = lists.get(lists.size() - 1);
                currentGrouping.add(sequenceEvent);
            }
        };
    }

    @Override
    public BinaryOperator<List<List<SequenceEvent>>> combiner() {
        return null;
    }

    @Override
    public Function<List<List<SequenceEvent>>, List<List<SequenceEvent>>> finisher() {
        return lists -> lists;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }
}
