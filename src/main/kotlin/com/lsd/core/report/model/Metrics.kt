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
        val allMessages = events.filterIsInstance<Message>()
        return listOfNotNull(
            Metric("Time to generate sequence diagram", sequenceDuration.pretty()),
            Metric("Time to generate component diagram", componentDuration.pretty()),
            Metric("Events captured", "${events.size}"),
            Metric("Messages captured", "${allMessages.size}"),
            Metric(
                "Top bottlenecks", allMessages.let(::createTree).asList
                    .drop(1)
                    .filter { it.isolatedDuration > Duration.ZERO }
                    .sortedBy { it.isolatedDuration }
                    .reversed()
                    .take(max)
                    .joinToString(separator = "") { node ->
                        """
                        |<details>
                        |  <summary>${node.request?.to?.componentName?.normalisedName} (isolated duration ${node.isolatedDuration.pretty()})</summary>
                        |  <ul>
                        |    ${node.request?.let { "<li>${it.details()} ${it.show()} ${it.open()}</li>" } ?: ""}
                        |    ${node.response?.let { "<li>${it.details()} ${it.show()} ${it.open()}</li>" } ?: ""}
                        |    ${if (node.children.isNotEmpty()) "<li>Children ${node.childrenDuration.pretty()}</li>" else ""}
                        |    <li>Total ${node.duration.pretty()}</li>
                        |  </ul>
                        |</details>
                        |""".trimMargin()
                    }).takeIf { it.value.isNotEmpty() },
        )
    }
}

private fun Duration.pretty(): String = toString()
    .substring(2)
    .replace(Pattern.compile("(\\d[HMS])(?!$)").toRegex(), "$1 ")
    .lowercase()

private fun Message.open() =
    """<a href="#${id}"><sub>[open]</sub></a>"""

private fun Message.show() =
    """<a href="javascript:scrollIntoViewFor('$id');"><sub>[show]</sub></a>"""

private fun Message.details() =
    """[${from.alias ?: from.componentName.normalisedName} -> ${to.alias ?: to.componentName.normalisedName}] ${duration?.pretty() ?: "0s"}"""

data class Metric(val key: String, val value: String)

fun createTree(messages: List<Message>): TreeNode {
    return TreeNode().also { root ->
        messages.fold(root) { node, message ->
            val nodeName = node.name
            val parentNodeName = node.parent?.name
            val from = message.from.componentName.normalisedName
            val to = message.to.componentName.normalisedName

            when {
                // response to current node
                to == nodeName -> {
                    node.response = message
                    node.parent ?: node
                }
                // response to parent node
                to == parentNodeName -> {
                    node.parent.response = message
                    node.parent
                }

                // sibling node
                from == nodeName && node.parent != null -> {
                    val child = TreeNode(request = message, parent = node.parent)
                    node.parent.children.add(child)
                    node.parent
                }

                // New child under this node
                else -> {
                    val child = TreeNode(request = message, parent = node)
                    node.children.add(child)
                    child
                }
            }
        }
    }
}

data class TreeNode(
    val request: Message? = null,
    var response: Message? = null,
    val parent: TreeNode? = null,
    val children: MutableList<TreeNode> = arrayListOf()
) {
    val duration: Duration
        get() = Duration.ofMillis(
            (request?.duration?.toMillis() ?: 0) + (response?.duration?.toMillis() ?: 0)
        )
    val childrenDuration: Duration get() = Duration.ofMillis(children.sumOf { it.duration.toMillis() })
    val isolatedDuration: Duration get() = duration.minus(childrenDuration)
    val asList get() : List<TreeNode> = listOf(this) + children.flatMap { it.asList }

    internal val name get() = request?.from?.componentName?.normalisedName

    override fun toString() = """
        TreeNode {
            name: $name,
            request: [${request?.from?.componentName?.normalisedName} -> ${request?.to?.componentName?.normalisedName}],
            response: [${response?.from?.componentName?.normalisedName} -> ${response?.to?.componentName?.normalisedName}],
            parent: $parent,
            duration: ${duration.pretty()},
            isolatedDuration: ${isolatedDuration.pretty()},
            children: ${children.size},
        }
    """.trimIndent()
}
