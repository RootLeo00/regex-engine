from colorama import Fore, Style
class RadixNode:
    def __init__(self, prefix: str = "", is_leaf: bool = False) -> None:
        # Mapping from the first character of the prefix of the node
        self.nodes: dict[str, RadixNode] = {}

        # A node will be a leaf if the tree contains its word
        self.is_leaf = is_leaf

        self.prefix = prefix

        # List of indices of tokens that match this prefix
        self.indices = []

    # ... (other methods remain the same)
    def match(self, word: str) -> tuple[str, str, str]:
        """Compute the common substring of the prefix of the node and a word

        Args:
            word (str): word to compare

        Returns:
            (str, str, str): common substring, remaining prefix, remaining word

        >>> RadixNode("myprefix").match("mystring")
        ('my', 'prefix', 'string')
        """
        x = 0
        for q, w in zip(self.prefix, word):
            if q != w:
                break

            x += 1

        return self.prefix[:x], self.prefix[x:], word[x:]
    def insert_token(self, token: str, index: int) -> None:
        """Insert a token (word) into the radix tree with its index"""
        if self.prefix == token and not self.is_leaf:
            self.is_leaf = True
            self.indices.append(index)
        elif token[0] not in self.nodes:
            self.nodes[token[0]] = RadixNode(prefix=token, is_leaf=True)
            self.nodes[token[0]].indices.append(index)
        else:
            incoming_node = self.nodes[token[0]]
            matching_string, remaining_prefix, remaining_token = incoming_node.match(token)
            if remaining_prefix == "":
                if remaining_token == "":
                    # This node represents the token, add the index
                    incoming_node.indices.append(index)
                else:
                    # Recursively insert the remaining part of the token
                    incoming_node.insert_token(remaining_token, index)
            else:
                incoming_node.prefix = remaining_prefix
                aux_node = self.nodes[matching_string[0]]
                self.nodes[matching_string[0]] = RadixNode(matching_string, False)
                self.nodes[matching_string[0]].nodes[remaining_prefix[0]] = aux_node
                if remaining_token == "":
                    self.nodes[matching_string[0]].is_leaf = True
                    self.nodes[matching_string[0]].indices.append(index)
                else:
                    self.nodes[matching_string[0]].insert_token(remaining_token, index)

    def search_tokens(self, token: str) -> list:
        """Search for a token in the radix tree and return its indices"""
        incoming_node = self.nodes.get(token[0], None)
        if not incoming_node:
            return []
        else:
            matching_string, remaining_prefix, remaining_token = incoming_node.match(token)
            if remaining_prefix != "":
                return []
            elif remaining_token == "":
                return incoming_node.indices
            else:
                return incoming_node.search_tokens(remaining_token)

def color_matched_words(words, matched_indices):
    colored_words = []
    for i, word in enumerate(words):
        if i in matched_indices:
            colored_word = f"{Fore.RED}{word}{Style.RESET_ALL}"
        else:
            colored_word = word
        colored_words.append(colored_word)
    return colored_words

def write_to_file(file_path, content):
    try:
        with open(file_path, "w") as file:
            file.write(content)
        print(f"Content has been written to {file_path}")
    except Exception as e:
        print(f"Error: {e}")

import sys

def debug():
    root = RadixNode()
    # Load the cache-file and build the radix tree with token indices
    cache_file = open("../input/book-about-babylone.txt", "r").read().split()
    #cache_file = ["this", "is", "a", "sample", "sentence", "ciao", "ciao", "amore"]
    for index, token in enumerate(cache_file):
        root.insert_token(token, index)

    # Example regular expression to search
    # pattern = "ciao.*amore"
    pattern= "Gutenberg"

    # Split the regular expression into components (you may need a proper regex parser)
    components = pattern.split(".*")

    # Search for each component and accumulate the matching indices
    matching_indices = []
    for component in components:
        component_indices = root.search_tokens(component)
        matching_indices.extend(component_indices)

    # Print the matched indices
    print("Matching indices:", matching_indices)
    colored_words = color_matched_words(cache_file, matching_indices)
    print(" ".join(colored_words))
    #write on ouput file
    write_to_file("../output/output_radixtree.txt", " ".join(colored_words))

import time
import pandas as pd
def test():
    print("---------[TEST] RADIXTREE SEARCH with args: ", sys.argv[1:], "-----------")
    filename=sys.argv[1]
    pattern=sys.argv[2]
    #create pandas table to store the results with columns: number of characters in filename, pattern, time_elapsed
    df = pd.DataFrame(columns=['ncharacters', 'pattern_len', 'time_elapsed'])

    try:
        txt = open(filename, "r").read()
        for i in range(100, 100000, 1000):
            print("test with ncharacters: ", i)
            df = testtiming(txt[:i], pattern, df)

        print(df)
        #store df in a pickle file
        df.to_pickle("../output/output_radixtree_textlength.pkl")
        #test with different pattern length
        df = pd.DataFrame(columns=['ncharacters', 'pattern_len', 'time_elapsed'])
        pattern=txt[:10000]
        print("pattern: ", len(pattern))
        for i in range(5, 5000, 10): #step 10
            print("test with pattern length: ", i)
            df = testtiming(txt[:10000], pattern[:i], df)
        df.to_pickle("../output/output_radixtree_patternlength.pkl")
        
    except Exception as e:
        print(f"Error: {e}")

def testtiming(cache_file, pattern, df):
    ncharacters= len(cache_file)
    start =time.time()
    root = RadixNode()

    for index, token in enumerate(cache_file):
        root.insert_token(token, index)

    # Split the regular expression into components (you may need a proper regex parser)
    components = pattern.split(".*")

    # Search for each component and accumulate the matching indices
    matching_indices = []
    for component in components:
        component_indices = root.search_tokens(component)
        matching_indices.extend(component_indices)

    # Print the matched indices
    # colored_words = color_matched_words(cache_file, matching_indices)
    # print("".join(colored_words))
    time_elapsed = time.time() - start
    

    newdf= pd.DataFrame([[ncharacters, len(pattern), time_elapsed]], columns=['ncharacters', 'pattern_len', 'time_elapsed'])
    df = pd.concat([df,newdf], ignore_index=True)
    return df


def main():
    print("---------RADIXTREE SEARCH with args: ", sys.argv[1:], "-----------")
    filename=sys.argv[1]
    pattern=sys.argv[2]
    try:
        root = RadixNode()

        cache_file = open(filename, "r").read().split()

        for index, token in enumerate(cache_file):
            root.insert_token(token, index)

        # Split the regular expression into components (you may need a proper regex parser)
        components = pattern.split(".*")

        # Search for each component and accumulate the matching indices
        matching_indices = []
        for component in components:
            component_indices = root.search_tokens(component)
            matching_indices.extend(component_indices)

        # Print the matched indices
        colored_words = color_matched_words(cache_file, matching_indices)
        print("".join(colored_words))
        
    except Exception as e:
        print(f"Error: {e}")

if __name__ == "__main__":
    # main()
    test()
