import pandas as pd
import matplotlib.pyplot as plt
import os

def plot_timing(file, x, y):
    # Initialize lists to store dataframes and plot labels
    listdf = []
    name = []

    # Create a figure for the plot
    fig = plt.figure(figsize=(9, 9))
    plt.subplots_adjust(left=0.25, bottom=0.15)  # Adjust subplot position

    print("plotting: ", file)
    i = 0

    # Loop through input files
    for f in file:
        # If the file ends with 'pkl', read it as a pickle file
        if os.path.splitext(f)[1] == ".pkl":
            listdf.append(pd.read_pickle(f))
        # If the file ends with 'csv', read it as a CSV file
        if os.path.splitext(f)[1] == ".csv":
            listdf.append(pd.read_csv(f))

        # Retrieve engine name and pattern from the file path
        tmp = f.split("/")[-1]
        name.append(tmp.split("_")[1])

        # Plot xtick every 10 elements
        df = listdf[i]
        plt.scatter(df[x][::5], df[y][::5], label=name[i].upper(), marker='o')  # Scatter plot with labels
        plt.plot(df[x], df[y])  # Line plot
        i += 1

    # Set the font size for x and y ticks
    plt.xticks(fontsize=12)
    plt.yticks(fontsize=12)

    plt.legend()  # Show legend
    plt.xlabel(x, fontsize=12)  # Set x-axis label
    plt.ylabel(y, fontsize=12)  # Set y-axis label

    # Save the plot as an image and display it
    plt.savefig('./graphs/' + x + '_' + y + '.png')
    plt.show()

if __name__ == "__main__":
    # List of input files
    file = [
        "./output/finaltests/output_automa_patternlength.csv",
        "./output/finaltests/output_kmp_patternlength.pkl",
        "./output/finaltests/output_radixtree_patternlength.pkl",
    ]

    # verify that the file exists
    for f in file:
        if not os.path.isfile(f):
            print("Error: file not found: ", f)
            exit()

    # Create a plot for pattern length vs. time elapsed
    plot_timing(file, "pattern_len", "time_elapsed")

    # List of input files
    file = [
        "./output/finaltests/output_automa_textlength.csv",
        "./output/finaltests/output_radixtree_textlength.pkl",
        "./output/finaltests/output_kmp_textlength.pkl"
    ]

    # Create a plot for text length vs. time elapsed
    plot_timing(file, "ncharacters", "time_elapsed")
