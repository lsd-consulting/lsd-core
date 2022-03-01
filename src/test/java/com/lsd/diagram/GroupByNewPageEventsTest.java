package com.lsd.diagram;

import com.lsd.events.Message;
import com.lsd.events.Newpage;
import com.lsd.events.PageTitle;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class GroupByNewPageEventsTest {

    @Test
    void splitsStreamByNewPageEvents() {
        var message1 = Message.builder().id("1").from("A").to("B").build();
        var message2 = Message.builder().id("2").from("B").to("A").build();
        var message3 = Message.builder().id("3").from("B").to("C").build();
        var newpage = Newpage.titled("Part B");

        var sequenceEvents = List.of(message1, message2, newpage, message3);

        var splitEvents = sequenceEvents.stream().collect(new GroupByNewPageEvents());

        assertThat(splitEvents).containsExactly(
                List.of(message1, message2), 
                List.of(PageTitle.titled("Part B"), message3)
        );
    }

}