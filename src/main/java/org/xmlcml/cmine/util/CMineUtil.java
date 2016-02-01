package org.xmlcml.cmine.util;

import java.util.ArrayList;
import java.util.List;

import org.xmlcml.xml.XMLUtil;

import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.Multisets;

import nu.xom.Node;

/** mainly static tools.
 * 
 * @author pm286
 *
 */
public class CMineUtil {

	/** sort entrySet by count.
	 * convenience method.
	 * @param wordSet
	 * @return
	 */
	public static Iterable<Multiset.Entry<String>> getEntriesSortedByCount(Multiset<String> wordSet) {
		return Multisets.copyHighestCountFirst(wordSet).entrySet();
	}

	public static Iterable<Entry<String>> getEntriesSortedByValue(Multiset<String> wordSet) {
		return  ImmutableSortedMultiset.copyOf(wordSet).entrySet();
	}
	
	/** extracts a list of attribute values.
	 * 
	 * @return
	 */
	public static List<String> getAttributeValues(Node searchNode, String xpath) {
		List<Node> nodes = XMLUtil.getQueryNodes(searchNode, xpath);
		List<String> nodeValues = new ArrayList<String>();
		for (Node node : nodes) {
			String value = node.getValue();
			if (value != null && value.trim().length() != 0) {
				nodeValues.add(value);
			}
		}
		return nodeValues;
	}


}
