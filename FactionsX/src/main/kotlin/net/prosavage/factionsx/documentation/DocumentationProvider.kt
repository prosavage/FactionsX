package net.prosavage.factionsx.documentation

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.PlayerAction
import net.savagelabs.autodoc.docs.DocCommand
import net.savagelabs.autodoc.docs.DocPermission
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.StringBuilder

abstract class DocumentationProvider {

    abstract fun getIdentifier(): String

    abstract fun getCommands(): List<DocCommand>

    abstract fun getPermissions(): List<DocPermission>

    fun generateCommandContent(): String {
        val commandContent = StringBuilder()
        getCommands().forEach { command ->
            commandContent.append("<h2>Command /${command.name}</h2>")
            commandContent.append("<p>${removeColorFromString(command.helpInfo)}</p>")
            commandContent.append("<ul>\n")
            processSubCommands(command, commandContent, 0)
            commandContent.append("</ul>\n")
        }
        return commandContent.toString()
    }

    fun generatePermissionContent(): String {
        val permissionContent = StringBuilder()
        permissionContent.append("<table>\n")
        permissionContent.append("<tr>" +
                "<th> Permission Node </th> <th> Description </th> <th> Default </th> </tr>\n")
        getPermissions().forEach { permission ->
            permissionContent.append("<tr> <td>${permission.permissionNode} </td><td> ${permission.description}  </td><td>" +
                    " ${permission.permissionDefault} </td></tr>\n")
        }
        permissionContent.append("</table>\n")
        return permissionContent.toString()
    }

    fun writeDocFile(name: String, content: String) {
        File(FactionsX.instance.dataFolder, "docs" + "/${name}.html").also {
            it.parentFile.mkdirs()
            it.createNewFile()
        }.writeText(content)
    }

    fun generateFactionPermissionsContent(): String {
        val fpermContent = StringBuilder()
        fpermContent.append("<h1>Player Actions</h1>\n")
        fpermContent.append("<p>These actions are universal for role, and relation based configuration.")
        fpermContent.append("<table>\n")
        fpermContent.append("<tr>" +
                "<th> Action Name </th> <th> Icon </th> </tr>\n")
        PlayerAction.values().forEach { action ->
            fpermContent.append("<tr> <td>${action.actionName} </td><td> ${action.icon}  </td> </tr>\n")
        }
        fpermContent.append("</table>\n")

        fpermContent.append("<h1>Member Actions</h1>\n")
        fpermContent.append("<p>These actions only apply to role based configuration for faction members.")
        fpermContent.append("<table>\n")
        fpermContent.append("<tr>" +
                "<th> Action Name </th> <th> Icon </th> </tr>\n")
        MemberAction.values().forEach { action ->
            fpermContent.append("<tr> <td>${action.actionName} </td><td> ${action.icon}  </td> </tr>\n")
        }
        fpermContent.append("</table>\n")

        return fpermContent.toString()
    }

    fun generateDocs() {
        // commands.
        writeDocFile("commands", generateCommandContent())
        // permissions.
        writeDocFile("permissions", generatePermissionContent())
        writeDocFile("wiki", generateWikiRedirect())
        writeDocFile("f-perm-values", generateFactionPermissionsContent())
    }

    fun generateWikiRedirect(): String {
        return "<script>\n" +
                "    window.location.href = \"https://savagelabs.net/docs/\"\n" +
                "</script>\n"
    }

    private fun processSubCommands(command: DocCommand, commandString: StringBuilder, depth: Int) {
        for (child in command.children) {
            // append tabs for depth.
            repeat(depth) {
                commandString.append("\t")
            }

            val prefix = if (depth == 0) {
                "/${command.name} "
            } else ""

            val format = ("<li> ${prefix}${child.name} - ${child.helpInfo}</li>\n")

            // colors and then uses stripColor to remove color codes :)
            commandString.append(removeColorFromString(format))
            if (child.children.isNotEmpty()) {
                commandString.append("<ul>")
                processSubCommands(child, commandString, depth + 1)
                commandString.append("</ul>")
            }
        }
    }

    private fun removeColorFromString(format: String): String {
        return ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', format))!!
    }

}