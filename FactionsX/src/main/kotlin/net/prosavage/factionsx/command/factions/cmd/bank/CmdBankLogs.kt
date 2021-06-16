package net.prosavage.factionsx.command.factions.cmd.bank

import net.prosavage.factionsx.command.engine.CommandInfo
import net.prosavage.factionsx.command.engine.CommandRequirementsBuilder
import net.prosavage.factionsx.command.engine.FCommand
import net.prosavage.factionsx.core.Faction
import net.prosavage.factionsx.core.Permission
import net.prosavage.factionsx.manager.PlayerManager.getFPlayer
import net.prosavage.factionsx.persist.Message
import net.prosavage.factionsx.persist.config.EconConfig
import net.prosavage.factionsx.util.MemberAction
import net.prosavage.factionsx.util.TimeHelper.prettyFormat
import org.apache.commons.lang.WordUtils
import java.text.SimpleDateFormat
import java.util.*

class CmdBankLogs : FCommand() {
    private val dateFormat = SimpleDateFormat(EconConfig.bankLogDateFormat)

    init {
        aliases += "logs"
        aliases += "log"

        optionalArgs += Argument("page", 0, IntArgument())

        commandRequirements = CommandRequirementsBuilder()
                .asFactionMember(true)
                .withPermission(Permission.BANK_LOGS)
                .withMemberAction(MemberAction.BANK_LOGS)
                .build()
    }

    override fun execute(info: CommandInfo): Boolean {
        if (!CmdBank.economyCheck(info)) {
            return false
        }

        val faction = info.faction ?: return false
        val pages = faction.getSortedBankLogPages()

        if (pages.isEmpty()) {
            info.message(Message.commandBankLogEmpty)
            return false
        }

        val page = if (info.args.isEmpty()) 1 else info.getArgAsInt(0) ?: 1
        val logs = pages[page] ?: kotlin.run {
            info.message(Message.commandBankLogPageNotFound, page.toString())
            return false
        }

        info.message(EconConfig.bankLogFormatHeader, page.toString(), pages.size.toString())
        logs.forEach { log ->
            val logArguments = generateLogItem(log).toTypedArray()
            info.message(if (log.type == Faction.BankLogType.PAY) EconConfig.bankLogPayFormat.format(*logArguments) else EconConfig.bankLogFormat.format(*logArguments))
        }

        return true
    }

    private fun generateLogItem(log: Faction.BankLog): LinkedList<String> = LinkedList<String>().apply {
        add(log.id.toString())
        add(getFPlayer(log.uuid)?.name ?: "Unknown")
        add(log.amount.toString())
        add(WordUtils.capitalizeFully(log.type.name))
        add((System.currentTimeMillis() - log.eventAt).prettyFormat())

        if (log.type == Faction.BankLogType.PAY) {
            add(this[4])
            set(4, log.target ?: "Unknown")
        }
    }

    override fun getHelpInfo(): String = Message.commandBankLogHelp
}