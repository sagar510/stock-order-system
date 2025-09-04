package com.example.stockordersystem.core.search;

import java.util.*;

public class Trie {
    private final TrieNode root = new TrieNode();

    public void insert(String word, int stockKey) {
        TrieNode node = root;
        for (char ch : word.toCharArray()) {
            node = node.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        node.isEnd = true;
        node.stockKeys.add(stockKey);
    }

    public List<Integer> prefixSearch(String prefix, int limit) {
        TrieNode node = root;
        for (char ch : prefix.toCharArray()) {
            node = node.children.get(ch);
            if (node == null) return Collections.emptyList();
        }
        List<Integer> result = new ArrayList<>();
        collect(node, result, limit);
        return result;
    }

    private void collect(TrieNode node, List<Integer> result, int limit) {
        if (result.size() >= limit) return;
        if (node.isEnd) {
            for (Integer key : node.stockKeys) {
                if (result.size() < limit) result.add(key);
                else return;
            }
        }
        for (TrieNode child : node.children.values()) {
            collect(child, result, limit);
            if (result.size() >= limit) return;
        }
    }

    static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
        List<Integer> stockKeys = new ArrayList<>();
    }
}
