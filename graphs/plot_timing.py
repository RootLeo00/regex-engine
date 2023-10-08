import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import os

def clear_data(df, dataname, min, max, std_threshold):
    # Filter by time_elapsed within the specified range
    df = df[(df[dataname] >= min) & (df[dataname] <= max)]

    # Calculate the mean and standard deviation of time_elapsed
    mean_time = df[dataname].mean()
    std_time = df[dataname].std()

    # Filter out rows where time_elapsed deviates significantly from the mean
    df = df[abs(df[dataname] - mean_time) <= std_threshold * std_time]
    return df


def plot_timing(file, x, y):
    # Initialize lists to store dataframes and plot labels
    listdf = []
    name = []

    # Create a figure for the plot
    fig = plt.figure(figsize=(9, 9))
    plt.subplots_adjust(left=0.15, bottom=0.1)  # Adjust subplot position

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
        
        # clear time data
        df=clear_data(df, "time_elapsed", 0, 60, 2)

        plt.scatter(df[x][::5], df[y][::5], label=name[i].upper(), marker='o')  # Scatter plot with labels

        # compute lower bound and upper bound to plot the variance
        df_err = df.sem() #Return unbiased standard error of the mean over requested axis.
        lower_bound = df[y] - df_err[y]
        upper_bound = df[y] + df_err[y]

        plt.fill_between(df[x].values.tolist(), lower_bound.values.tolist(), upper_bound.values.tolist(), alpha=0.3,  facecolor='yellow', label="Standard Error of the Mean" if i == 0 else "")
        
        plt.plot(df[x], df[y])  

        i += 1

    # Set the font size for x and y ticks
    plt.xticks(fontsize=15)
    plt.yticks(fontsize=15)

    plt.legend(fontsize=15)  # Show legend

    # Set x-axis label
    if x == "pattern_len":
        plt.xlabel("Pattern length", fontsize=15)
    elif x == "ncharacters":
        plt.xlabel("Text length", fontsize=15)
    elif x == "time_elapsed":
        plt.xlabel("Time [s]", fontsize=15) 
    else:
        plt.xlabel(x, fontsize=15)  # Set x-axis label
    
    # Set y-axis label
    if y == "pattern_len":
        plt.ylabel("Pattern length", fontsize=15)
    elif y == "ncharacters":
        plt.ylabel("Text length", fontsize=15)
    elif y == "time_elapsed":
        plt.ylabel("Time [s]", fontsize=15) 
    else:
        plt.ylabel(y, fontsize=12) 
    

    # Save the plot as an image and display it
    names_engine= "_".join(name)
    plt.savefig('./graphs/' + x + '_' + y  + '_' +names_engine+ '.png')
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
        "./output/finaltests/output_kmp_textlength.pkl",
        "./output/finaltests/output_radixtree_textlength.pkl",
    ]

    # Create a plot for text length vs. time elapsed
    plot_timing(file, "ncharacters", "time_elapsed")
