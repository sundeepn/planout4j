package com.glassdoor.planout4j.compiler;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.glassdoor.planout4j.NamespaceConfig;
import com.glassdoor.planout4j.config.ValidationException;

/**
 * Interface to parsing Planout4j config expressed as either YAML or JSON.
 * @author ernest.mishkin
 */
public abstract class Planout4jConfigParser {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    /**
     * @return String representing parser's source (JSON or YAML)
     */
    public abstract String getConfigSource();

    /**
     * Parses input and constructs NamespaceConfig, performing validation along the way.
     * @param input source
     * @param namespace namespace name (optional)
     * @return NamespaceConfig parsed configuration
     * @throws ValidationException in case of config errors
     */
    public abstract NamespaceConfig parseAndValidate(final Reader input, String namespace) throws ValidationException;

    /**
     * Parses and validates a collection of namespaces.
     * @param namespaceConfigsRaw map of namespace name to its unparsed configuration
     * @return Map of namespace name to parsed NamespaceConfig
     * @throws ValidationException in case of at least one namespace failed to get parsed
     */
    public Map<String, NamespaceConfig> parseAndValidate(final Map<String, String> namespaceConfigsRaw) throws ValidationException {
        final List<String> invalidNamespaces = new ArrayList<>();
        final Map<String, NamespaceConfig> namespaceConfigsParsed = new HashMap<>();
        for (String namespace : namespaceConfigsRaw.keySet()){
            try {
                namespaceConfigsParsed.put(namespace,
                        parseAndValidate(new StringReader(namespaceConfigsRaw.get(namespace)), namespace));
            } catch (Exception e) {
                LOG.error("Error while parsing and validating {} for {}", getConfigSource(), namespace, e);
                invalidNamespaces.add(namespace);
            }
        }

        if (invalidNamespaces.isEmpty()) {
            return namespaceConfigsParsed;
        } else {
            throw new ValidationException("Unable to parse and validate namespace config " +
                    getConfigSource() + " for the following namespace: " + invalidNamespaces);
        }

    }

}
