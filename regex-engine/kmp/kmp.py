from colorama import Fore, Style
import sys
import time
import pandas as pd

def KMPSearch(pattern, txt, lps):
	"""
	Implements the Knuth-Morris-Pratt (KMP) string search algorithm to find all occurrences of a pattern in a given text (txt). 
	It returns a list of indices where the pattern matches in the text.
	"""
	M = len(pattern)
	N = len(txt)
	matching_indexes = []

	
	j = 0 # index for pattern[]

	i = 0 # index for txt[]
	while i < N:
		if pattern[j] == txt[i]:
			i += 1
			j += 1

		if j == M:
			# print ("Found pattern at index", str(i-j))
			matching_indexes.append(i-j)
			j = lps[j-1]

		# mismatch after j matches
		elif i < N and pattern[j] != txt[i]:
			# Do not match lps[0..lps[j-1]] characters,
			# they will match anyway
			if j != 0:
				j = lps[j-1]
			else:
				i += 1
	return matching_indexes

def computeLPSArray(pattern, M):
	""" Computes the Longest Prefix Suffix (LPS) array for a given pattern of length M. 
	This array helps in efficient pattern matching."""
	len = 0 # length of the previous longest prefix suffix
	lps = [0]*M

	i = 1
 
    # the loop calculates lps[i] for i = 1 to M-1
	while i < M:
		if pattern[i]== pattern[len]:
			len += 1
			lps[i] = len
			i += 1
		else:
			if len != 0:
				len = lps[len-1]
			else:
				lps[i] = 0
				i += 1
	#shift all the array by one position forward and assign lps[0] =-1
	lps.insert(0, -1)
	return lps


def computeCarryOverArray(pattern, M, lps):
	"""
		An optimization of computeLPSArray, 
		further enhancing the LPS array to avoid unnecessary comparisons.        
    """

	# the loop calculates lps[i] for i = 0 to M-1
	for i in range(1, M):
		if pattern[i]== pattern[lps[i]]:
			if lps[lps[i]] == -1:
				lps[i] = -1
			else:
				lps[i] = lps[lps[i]]
		else:
			lps[i]=lps[i]
	return lps

def print_color_matched_words(text, matched_indices, len_pattern):
	"""Formats and prints the text with matched patterns colored in red using the Colorama library. 
	It returns a colored version of the input text."""
	colored_text = ""
	current_index = 0

	for i in matched_indices:
		# Add the uncolored part of the text before the match
		colored_text += text[current_index:i]
		print(text[current_index:i], end="")

		# Add the colored part (match)
		colored_text += f"{Fore.RED}{text[i:i+len_pattern]}{Style.RESET_ALL}"
		print(f"{Fore.RED}{text[i:i+len_pattern]}{Style.RESET_ALL}", end="")

		# Update the current index
		current_index = i + len_pattern

    # Add the remaining uncolored part of the text
	colored_text += text[current_index:]
	print(text[current_index:], end="\n")

	return colored_text


def test(filename, pattern):
	"""Performs performance tests on the KMP string search algorithm. 
	It tests different numbers of characters and pattern lengths and records the time taken for each test case. 
	Results are stored in a pandas DataFrame and saved as a pickle file."""
	print("---------[TEST] KMP SEARCH with args: ", filename, pattern, "-----------")

	#create pandas table to store the results with columns: number of characters in filename, pattern, time_elapsed
	df = pd.DataFrame(columns=['ncharacters', 'pattern_len', 'time_elapsed'])

	try:
		# test with different number of characters
		txt = open(filename, "r").read()

		for i in range(1000, 100000 ,1000): #step 1000
			print("test with ncharacters: ", i)
			df = testtiming(txt[:i], pattern, df)

		print(df)
		df.to_pickle("../output/output_kmp_textlength.pkl")

		#test with different pattern length
		df = pd.DataFrame(columns=['ncharacters', 'pattern_len', 'time_elapsed'])
		pattern=txt[:10000]
		print("pattern: ", len(pattern))
		for i in range(5, 5000, 10): 
			print("test with pattern length: ", i)
			df = testtiming(txt[:10000], pattern[:i], df)
		df.to_pickle("../output/output_kmp_patternlength.pkl")
		print(df)
	except Exception as e:
		print(f"Error: {e}")


def testtiming(txt, pattern, df):
	"""Measures the time it takes to run the KMP search algorithm on a given text and pattern. 
	It records the time taken and returns the results as a pandas DataFrame."""
	lines = txt.split("\n")
	time_elapsed = 0.0
	#iterate over each line of lines and count the number of characters in each line
	ncharacters= len(txt)
	# Preprocess the pattern (calculate lps[] array)
	lps=computeLPSArray(pattern, len(pattern))
	lps=computeCarryOverArray(pattern, len(pattern), lps)

	for l in lines:
		start =time.time()
		matched_index= KMPSearch(pattern, l, lps)
		time_elapsed += time.time() - start

	newdf= pd.DataFrame([[ncharacters, len(pattern), time_elapsed]], columns=['ncharacters', 'pattern_len', 'time_elapsed'])
	df = pd.concat([df,newdf], ignore_index=True)
	return df

def main(filename, pattern):
	print("---------KMP SEARCH with args: ", filename, pattern, "-----------")
	
	try:
		lines = open(filename, "r").read().split("\n")
		lps=computeLPSArray(pattern, len(pattern))
		lps=computeCarryOverArray(pattern, len(pattern), lps)
		for l in lines:
			matched_index= KMPSearch(pattern, l, lps)
			print_color_matched_words(l, matched_index, len(pattern))
	
	except Exception as e:
		print(f"Error: {e}")


if __name__ == "__main__":
	filename=sys.argv[1]
	pattern=sys.argv[2]
	if len(sys.argv)==4:
		if sys.argv[3] == "test":
			test(filename=filename, pattern=pattern)
		else:
			print("Bad arguments. Usage: python3 kmp.py <filename> <pattern> [test]")
			exit()
	else:
		main(filename, pattern)