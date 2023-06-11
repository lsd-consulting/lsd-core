package com.lsd.core.report.model

import com.lsd.core.domain.Message
import com.lsd.core.domain.SequenceEvent
import java.time.Duration
import java.util.regex.Pattern

class Metrics(
    val events: List<SequenceEvent>,
    val sequenceDuration: Duration,
    val componentDuration: Duration
) {

    @JvmOverloads
    fun asList(max: Int = 5): List<Metric> {
        val allMessages = events.filterIsInstance(Message::class.java)
        return listOf(
            Metric("Time to generate sequence diagram", sequenceDuration.pretty()),
            Metric("Time to generate component diagram", componentDuration.pretty()),
            Metric("Events captured", "${events.size}"),
            Metric("Messages captured", "${allMessages.size}"),
            Metric("Top isolated durations", allMessages.let(::createTree).asList
                .drop(1)
                .sortedBy { it.isolatedDuration }
                .reversed()
                .take(max)
                .joinToString(separator = "<br>", prefix = "<ol>", postfix = "</ol>") { node ->
                    """<li>
                        | <ul>
                        |  <li>Isolated duration ${node.isolatedDuration.pretty()}</li>
                        |  <li>Children duration ${node.childrenDuration.pretty()}</li>
                        |  <li>Total duration ${node.duration.pretty()}</li>
                        |  ${node.request?.let { "<li>request ${it.messageHyperlink()}</li>" } ?: ""}
                        |  ${node.response?.let { "<li>response ${it.messageHyperlink()}</li>" } ?: ""}
                        | </ul>
                        |</li>""".trimMargin()
                }),
        )
    }
}

private fun Duration.pretty(): String = toString()
    .substring(2)
    .replace(Pattern.compile("(\\d[HMS])(?!$)").toRegex(), "$1 ")
    .lowercase()

private fun Message.messageHyperlink() =
    """<a href="#${id}">${duration?.pretty() ?: "0s"} [${from.componentName.normalisedName} -> ${to.componentName.normalisedName}]</a>"""

data class Metric(val name: String, val value: String)

fun createTree(messages: List<Message>): TreeNode {
    return TreeNode(request = null, response = null, parent = null).apply {
        messages.fold(this) { node, message ->
            val nodeComponent = node.request?.from?.componentName?.normalisedName
            val nodeParentComponent = node.parent?.request?.from?.componentName?.normalisedName
            val messageTo = message.to.componentName.normalisedName
            val messageFrom = message.from.componentName.normalisedName
            when  {
                // response to current node
                messageTo == nodeComponent -> {
                    node.response = message
                    node.parent ?: node
                }
                // response to parent node
                messageTo == nodeParentComponent -> {
                    node.parent.response = message
                    node.parent
                }

                // sibling node with same component
                messageFrom == nodeComponent -> {
                    val child = TreeNode(request = message, response = null, parent = node.parent)
                    node.parent!!.children.add(child)
                    node.parent
                }
                
                else -> {
                    val child = TreeNode(request = message, response = null, parent = node)
                    node.children.add(child)
                    child
                }
            }
        }
    }
}

data class TreeNode(
    val request: Message?,
    var response: Message?,
    val parent: TreeNode?,
    val children: MutableList<TreeNode> = arrayListOf()
) {
    val duration get() = Duration.ofMillis((request?.duration?.toMillis() ?: 0) + (response?.duration?.toMillis() ?: 0))
    val childrenDuration get() = Duration.ofMillis(children.sumOf { it.duration.toMillis() })
    val isolatedDuration get() = duration.minus(childrenDuration)
    val asList get() : List<TreeNode> = listOf(this) + children.flatMap { it.asList }

    override fun toString() = """
        TreeNode {
            request: [${request?.from?.componentName?.normalisedName} -> ${request?.to?.componentName?.normalisedName}],
            response: [${response?.from?.componentName?.normalisedName} -> ${response?.to?.componentName?.normalisedName}],
            parent: $parent,
            duration: ${duration.pretty()},
            isolatedDuration: ${isolatedDuration.pretty()},
            children: ${children.size},
        }
    """.trimIndent()
}
