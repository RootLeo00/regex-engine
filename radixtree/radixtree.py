from colorama import Fore, Style
import sys
import time
import pandas as pd

class RadixNode:
    def __init__(self, prefix: str = "", is_leaf: bool = False) -> None:
        # Mapping from the first character of the prefix of the node
        self.nodes: dict[str, RadixNode] = {}

        # A node will be a leaf if the tree contains its word
        self.is_leaf = is_leaf

        self.prefix = prefix

        # List of indices of tokens that match this prefix
        self.indices = []

    
    def match(self, word: str) -> tuple[str, str, str]:
        """ Computes the common substring between the prefix of the node and a 
        given word, returning the common substring, remaining prefix, and remaining word."""
        
        x = 0
        for q, w in zip(self.prefix, word):
            if q != w:
                break

            x += 1

        return self.prefix[:x], self.prefix[x:], word[x:]
    
    def insert_token(self, token: str, index: int) -> None:
        """Inserts a token (word) into the radix tree with its index. Handles cases where 
        the token partially or fully matches existing nodes in the tree."""

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
        """Searches for a token in the radix tree and returns a list of matching token indices."""
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



    def delete_tree(self):
        """Recursively delete all nodes in the radix tree."""
        for node in self.nodes.values():
            node.delete_tree()
        
        # Clear the current node
        self.nodes.clear()
        self.indices.clear()
        self.is_leaf = False
        self.prefix = ""

    def __str__(self, level=0):
        prefix_str = f"Prefix: {self.prefix}, Indices: {self.indices}"
        ret = "\t" * level + prefix_str + "\n"

        for char, node in self.nodes.items():
            ret += node.__str__(level + 1)

        return ret

def color_matched_words(words, matched_indices):
    """Given a list of words and a list of indices of matched words, 
    this function returns a list of colored words, 
    where matched words are colored in red using the Colorama library."""

    colored_words = []
    for i, word in enumerate(words):
        if i in matched_indices:
            colored_word = f"{Fore.RED}{word}{Style.RESET_ALL}"
        else:
            colored_word = word
        colored_words.append(colored_word)
    return colored_words

def write_to_file(file_path, content):
    """Writes the given content to a file specified by file_path."""
    try:
        with open(file_path, "w") as file:
            file.write(content)
        print(f"Content has been written to {file_path}")
    except Exception as e:
        print(f"Error: {e}")


def test(filename, pattern):
    """ A function to run performance tests on the Radix Tree search algorithm. 
    It tests different numbers of characters 
    and pattern lengths and records the time taken for each test case."""

    print("---------[TEST] RADIXTREE SEARCH with args: ", filename, pattern, "-----------")

    #create pandas table to store the results with columns: number of characters in filename, pattern, time_elapsed
    df = pd.DataFrame(columns=['ncharacters', 'pattern_len', 'time_elapsed'])

    try:
        txt = open(filename, "r").read()
        for i in range(100, 100000, 1000):
            print("test with ncharacters: ", i)
            df = testtiming(txt[:i], pattern, df)

        #store df in a pickle file
        df.to_pickle("./output/output_radixtree_textlength.pkl")
        #test with different pattern length
        df = pd.DataFrame(columns=['ncharacters', 'pattern_len', 'time_elapsed'])
        pattern=txt[:10000]
        print("pattern: ", len(pattern))
        for i in range(5, 5000, 10): #step 10
            print("test with pattern length: ", i)
            df = testtiming(txt[:10000], pattern[:i], df)
        df.to_pickle("./output/output_radixtree_patternlength.pkl")
        
    except Exception as e:
        print(f"Error: {e}")

def testtiming(cache_file, pattern, df):
    """Performs the timing test by building a Radix Tree from a cache file and 
    searching for a pattern. 
    It records the time taken and returns the results as a pandas DataFrame."""
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
    # print(" ".join(colored_words))
    time_elapsed = time.time() - start
    

    newdf= pd.DataFrame([[ncharacters, len(pattern), time_elapsed]], columns=['ncharacters', 'pattern_len', 'time_elapsed'])
    df = pd.concat([df,newdf], ignore_index=True)
    return df


def main(filename, pattern):
    print("---------RADIXTREE SEARCH with args: ", filename, pattern, "-----------")

    try:
        root = RadixNode()

        cache_file_lines = open(filename, "r").read().split("\n")

        for line in cache_file_lines:
            words=line.split()
            # Remove non-alphanumeric characters inside each word
            # Use re.sub to replace the matched characters with an empty string
            for index, token in enumerate(words):
                root.insert_token(token, index)

            # Search for each component and accumulate the matching indices
            matching_indices = []
            founded_indices = root.search_tokens(pattern)
            matching_indices.extend(founded_indices)

            # Print the matched indices
            colored_words = color_matched_words(words, matching_indices)
            print(" ".join(colored_words))
            root.delete_tree()


        
    except Exception as e:
        print(f"Error: {e}")


if __name__ == "__main__":
    filename=sys.argv[1]
    pattern=sys.argv[2]
        
    # check if pattern as only alfanumeric characters
    if not pattern.isalnum():
        print("Error: pattern must contain only alphanumeric characters")
        exit()

    if len(sys.argv)==4:
        if sys.argv[3] == "test":
            test(filename, pattern)
        else:
            print("Bad arguments. Usage: python3 radixtree.py <filename> <pattern> [test]")
            exit()
    else:
        main(filename, pattern)
