package me.schooltests.potatoolympics.core.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Scan a chat message and run it
 * against valid inputs to find
 * a match.
 *
 * @author Itzdlg (SchoolTests)
 */
@SuppressWarnings({"unused"})
public class ChatScanner {
    private JavaPlugin plugin;
    private HumanEntity scannerTarget;
    private String scannerPrompt;
    private int minLength = 1;
    private int maxLength = 0;
    private final List<String> matchSet = new ArrayList<>();

    private BiConsumer<String, String> handler;
    private Consumer<ChatScannerError> errorHandler;
    private int ticks = 20;

    public ChatScanner(final JavaPlugin plugin, final HumanEntity scannerTarget, final String scannerPrompt) {
        this.plugin = plugin;
        this.scannerTarget = scannerTarget;
        this.scannerPrompt = scannerPrompt;
    }

    /**
     * Gets the plugin that created this scanner
     * @return Plugin listeners are registered with
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Gets the player the scanner looks for
     * @return Target of scanner
     */
    public HumanEntity getScannerTarget() {
        return scannerTarget;
    }

    /**
     * The prompt given to the target
     *
     * @see ChatScanner#getScannerTarget()
     * @return Scanner Prompt
     */
    public String getScannerPrompt() {
        return scannerPrompt;
    }

    /**
     * Returns the minimum length input must be
     * @return Minimum length
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Sets the minimum length for the validator
     * @param minLength Minimum length for input
     * @return ChatScanner instance
     */
    public ChatScanner setMinLength(int minLength) {
        this.minLength = minLength;
        return this;
    }

    /**
     * Returns the maximum length input must be
     * @return Maximum length
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Sets the maximum length for the validator
     * @param maxLength Maximum length for input
     * @return ChatScanner instance
     */
    public ChatScanner setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        return this;
    }

    /**
     * Match input to a collection
     * @param matchSet The set of valid inputs
     * @return ChatScanner instance
     */
    public <T> ChatScanner match(Collection<T> matchSet) {
        match(matchSet, Object::toString);
        return this;
    }

    /**
     * Match input with a specified function to get the validation string
     * @param matchSet The set of valid inputs
     * @param method The method to get validation string
     * @return ChatScanner instance
     */
    public <T> ChatScanner match(Collection<T> matchSet, Function<Object, String> method) {
        matchSet.forEach(i -> this.matchSet.add(method.apply(i)));
        this.matchSet.sort(Comparator.comparing(String::length));
        return this;
    }

    /**
     * Match input to an array
     * @param matchSet The array of valid inputs
     * @return ChatScanner instance
     */
    public ChatScanner match(Object[] matchSet) {
        match(matchSet, Object::toString);
        return this;
    }

    /**
     * Match input with a specified function to get the validation string
     * @param matchSet The set of valid inputs
     * @param method The method to get validation string
     * @return ChatScanner instance
     */
    public ChatScanner match(Object[] matchSet, Function<Object, String> method) {
        for (int i = 0; i < matchSet.length; i++) this.matchSet.add(method.apply(matchSet[i]));
        this.matchSet.sort(Comparator.comparing(String::length));
        return this;
    }

    /**
     * Valid inputs
     * @return Match set the scanner validates
     */
    public List<String> getMatchSet() {
        return matchSet;
    }

    /**
     * The code ran after input is given
     * @param handler Consumer that takes in rawInput, match
     * @return ChatScanner instance
     */
    public ChatScanner handle(BiConsumer<String, String> handler) {
        this.handler = handler;
        return this;
    }

    /**
     * The code ran after an error is given
     * @param errorHandler Consumer that takes in error
     * @see ChatScanner.ChatScannerError;
     * @return ChatScanner instance
     */
    public ChatScanner ifError(Consumer<ChatScannerError> errorHandler) {
        this.errorHandler = errorHandler;
        return this;
    }

    /**
     * Await input and then validate it
     * @param timeout The seconds the prompt will time out after
     */
    public void await(int timeout) {
        scannerTarget.sendMessage(ChatColor.translateAlternateColorCodes('&', scannerPrompt)); // Send the player the target prompt
        this.ticks = timeout * 20; // Set the ticks before the prompt cancels
        AtomicBoolean attempted = new AtomicBoolean(false); // Has the prompt been answered?
        final Listener listener = new Listener() {
            @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
            public void onChat(AsyncPlayerChatEvent event) {
                if (!attempted.get() && event.getPlayer().getUniqueId().equals(scannerTarget.getUniqueId())) { // Is the message from the target player and has it been attempted before
                    event.setCancelled(true);
                    attempted.set(true);

                    final String rawInput = event.getMessage().trim(); // Answer to the prompt
                    if (rawInput.equalsIgnoreCase("cancel")) {
                        errorHandler.accept(ChatScannerError.CANCELLED_SCANNER); // Cancel the prompt if cancel is given
                    } else {
                        if (event.getMessage().length() < minLength || (maxLength > 0 && event.getMessage().length() > maxLength)) { // The prompt is not within valid size
                            errorHandler.accept(ChatScannerError.INVALID_SIZE);
                        } else {
                            if (matchSet.isEmpty()) { // The match list is empty, therefore only raw input is allowed
                                handler.accept(rawInput, rawInput);
                            } else {
                                Optional<String> match = matchSet.stream().filter(i -> i.toLowerCase().replace("_", " ").startsWith(rawInput.toLowerCase().replace("_", " "))).findFirst();
                                if (match.isPresent())
                                    handler.accept(rawInput, match.get());
                                else
                                    errorHandler.accept(ChatScannerError.INVALID_INPUT); // The input does not match anything in the matcher
                            }
                        }
                    }
                }
            }
        };

        Bukkit.getPluginManager().registerEvents(listener, plugin);
        Bukkit.getScheduler().runTaskLater(plugin, () -> { // Ran only after the specified amount of ticks in #await()
            HandlerList.unregisterAll(listener);
            if (!attempted.get()) errorHandler.accept(ChatScannerError.TIME_RUN_OUT);
        }, ticks);
    }

    /**
     * Errors for the ChatScanner
     * @see ChatScanner
     */
    public enum ChatScannerError {
        TIME_RUN_OUT, INVALID_INPUT, CANCELLED_SCANNER, INVALID_SIZE;
        public String msg() {
            switch (this) {
                case TIME_RUN_OUT:
                    return "Your time has run out to enter a value!";
                case INVALID_INPUT:
                case INVALID_SIZE:
                    return "Please enter a valid input for the prompt!";
                case CANCELLED_SCANNER:
                    return "Chat input cancelled!";
                default:
                    return "Something went wrong!";
            }
        }
    }
}