package net.prosavage.factionsx.documentation

import net.prosavage.factionsx.FactionsX
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Permission
import net.savagelabs.autodoc.docs.DocCommand
import net.savagelabs.autodoc.docs.DocPermission

class FactionsXDocumentationProvider : DocumentationProvider() {

    override fun getIdentifier(): String {
        return "FactionsX"
    }

    override fun getCommands(): List<DocCommand> {
        val docCommand = generateDocCommand(FactionsX.baseCommand)
        val adminDocCommand = generateDocCommand(FactionsX.baseAdminCommand)
        return listOf(docCommand, adminDocCommand)
    }


    fun generateDocCommand(command: FCommand): DocCommand {
        val parentDocCommand = DocCommand(command.prefix, command.aliases, command.getHelpInfo(), mutableListOf())
        processDocCommands(command, parentDocCommand)
        return parentDocCommand
    }

    fun processDocCommands(fCommand: FCommand, docCommand: DocCommand) {
        for (cmd in fCommand.subCommands) {
            val loopDocCommand = DocCommand(cmd.aliases.firstOrNull() ?: cmd.prefix, cmd.aliases, cmd.getHelpInfo(), mutableListOf())
            if (cmd.subCommands.isNotEmpty()) {
                processDocCommands(cmd, loopDocCommand)
            }
            docCommand.addChild(loopDocCommand)
        }
    }


    override fun getPermissions(): List<DocPermission> {
        return Permission.values().map {
            DocPermission(
                it.getFullPermissionNode(),
                it.description,
                it.permissionDefault
            )
        }
    }

}